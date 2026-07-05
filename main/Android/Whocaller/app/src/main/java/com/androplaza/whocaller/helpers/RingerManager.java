/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.androplaza.whocaller.R;

public class RingerManager {
    private static final String TAG = "RingerManager";
    private static RingerManager instance;
    private Context context;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private PowerManager.WakeLock wakeLock;
    private boolean isRinging = false;
    private boolean isVibrating = false;

    private RingerManager(Context context) {
        this.context = context.getApplicationContext();
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            // Use SCREEN_BRIGHT_WAKE_LOCK to ensure screen turns on and stays on
            this.wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, 
                "Whocaller:RingerWakeLock"
            );
        }
    }

    public static synchronized RingerManager getInstance(Context context) {
        if (instance == null) {
            instance = new RingerManager(context);
        }
        return instance;
    }

    public void startRinging() {
        if (isRinging) {
            Log.d(TAG, "Ringer already active, ignoring duplicate start request");
            return;
        }

        // Ensure any existing ringtone is stopped first
        stopRingtone();

        Log.d(TAG, "Starting ringer");
        isRinging = true;

        // Acquire wake lock
        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire();
        }

        // Start vibration
        startVibration();

        // Start ringtone
        startRingtone();
    }

    public void stopRinging() {
        if (!isRinging) {
            return;
        }

        Log.d(TAG, "Stopping ringer");
        isRinging = false;

        // Release wake lock
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }

        // Stop vibration
        stopVibration();

        // Stop ringtone
        stopRingtone();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void startRingtone() {
        try {
            // Get the default ringtone URI
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            
            // If no default ringtone, use a fallback
            if (ringtoneUri == null) {
                ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            if (ringtoneUri != null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(context, ringtoneUri);
                
                // Set audio attributes for proper audio routing
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();
                    mediaPlayer.setAudioAttributes(audioAttributes);
                } else {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                }

                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
                
                Log.d(TAG, "Ringtone started successfully");
            } else {
                Log.e(TAG, "No ringtone URI available");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting ringtone: " + e.getMessage());
            // Fallback to system ringtone
            try {
                Ringtone ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                if (ringtone != null) {
                    ringtone.setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build());
                    ringtone.play();
                }
            } catch (Exception ex) {
                Log.e(TAG, "Fallback ringtone also failed: " + ex.getMessage());
            }
        }
    }

    private void stopRingtone() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping ringtone: " + e.getMessage());
        }
    }

    private void startVibration() {
        if (vibrator != null && vibrator.hasVibrator()) {
            isVibrating = true;
            
            // Create vibration pattern: wait 0ms, vibrate 1000ms, wait 1000ms, repeat
            long[] pattern = {0, 1000, 1000};
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                VibrationEffect vibrationEffect = VibrationEffect.createWaveform(pattern, 0);
                vibrator.vibrate(vibrationEffect);
            } else {
                vibrator.vibrate(pattern, 0);
            }
            
            Log.d(TAG, "Vibration started");
        }
    }

    private void stopVibration() {
        if (vibrator != null && isVibrating) {
            vibrator.cancel();
            isVibrating = false;
            Log.d(TAG, "Vibration stopped");
        }
    }

    public boolean isRinging() {
        return isRinging;
    }

    public void setAudioMode(int mode) {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setMode(mode);
                Log.d(TAG, "Audio mode set to: " + mode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting audio mode: " + e.getMessage());
        }
    }

    public void setSpeakerphoneOn(boolean on) {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setSpeakerphoneOn(on);
                Log.d(TAG, "Speakerphone set to: " + on);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting speakerphone: " + e.getMessage());
        }
    }
} 