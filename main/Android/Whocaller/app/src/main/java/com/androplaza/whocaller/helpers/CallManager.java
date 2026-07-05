package com.androplaza.whocaller.helpers;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.telecom.VideoProfile;
import android.widget.Toast;

import com.androplaza.whocaller.R;

import java.util.ArrayList;
import java.util.List;

public class CallManager {
    public static final String ACTION_CALL = "action_call";
    public static final String ACTION_TIME = "action_time";
    private static List<Call> activeCalls = new ArrayList<>();
    public static Handler handler;
    public static String num;
    public static int status;
    public static int time;
    public AudioManager am;
    private final Context context;
    private boolean hold = false;
    public static InCallService inCallService;

    public CallManager(Context context) {
        this.context = context;
        this.am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // Note: Audio mode will be set when call becomes active to avoid interfering with ringing
    }

    public void setCall(Call call) {
        if (call != null) {
            call.registerCallback(callCallback);
            activeCalls.add(call);
        }
    }

    public void setCallAudioMode() {
        // Set audio mode for active calls only
        if (this.am != null) {
            this.am.setMode(AudioManager.MODE_IN_CALL);
        }
    }

    public static void holdCall(Call call) {
        if (call != null && call.getState() == Call.STATE_ACTIVE) {
            call.hold();
            Toast.makeText(inCallService, "Call on hold", Toast.LENGTH_SHORT).show();
        }
    }

    public static void unholdCall(Call call) {
        if (call != null && call.getState() == Call.STATE_HOLDING) {
            call.unhold();
            Toast.makeText(inCallService, "Call resumed", Toast.LENGTH_SHORT).show();
        }
    }

    public static List<Call> getActiveCalls() {
        return activeCalls;
    }

    public void answer() {
        if (!activeCalls.isEmpty()) {
            activeCalls.get(activeCalls.size() - 1).answer(VideoProfile.STATE_AUDIO_ONLY);
        }
    }

    public void holdAndResume(Call callToHold, Call callToResume) {
        if (callToHold != null) callToHold.hold();
        if (callToResume != null) callToResume.unhold();
    }

    public void mergeCalls() {
        if (activeCalls.size() >= 2) {
            Call firstCall = activeCalls.get(0);
            Call secondCall = activeCalls.get(1);
            firstCall.conference(secondCall);
        }
    }


    public static void muteCall(boolean isMuted) {
        inCallService.setMuted(isMuted);
        Toast.makeText(inCallService, isMuted ? "Call muted" : "Call unmuted", Toast.LENGTH_SHORT).show();
    }

    public static void speakerCall(boolean isSpeakerOn) {
        int route = isSpeakerOn ? CallAudioState.ROUTE_SPEAKER : CallAudioState.ROUTE_EARPIECE;
        inCallService.setAudioRoute(route);
        Toast.makeText(inCallService, isSpeakerOn ? "Speaker on" : "Speaker off", Toast.LENGTH_SHORT).show();
    }

    public static void hangUpCallold(Call call) {
        if (call != null) {
            call.disconnect();
            activeCalls.remove(call);
        }
    }

    public static void hangUpCall() {
        if (!activeCalls.isEmpty()) {
            if (activeCalls.size() == 1) {
                // If only one call, disconnect it completely
                Call lastCall = activeCalls.get(0);
                lastCall.disconnect();
                activeCalls.clear();
                // Reset audio mode when all calls end
                resetAudioMode();
            } else {
                // If multiple calls, disconnect only the last one
                Call lastCall = activeCalls.remove(activeCalls.size() - 1);
                lastCall.disconnect();

                try {
                    num = activeCalls.get(0).getDetails().getHandle().getSchemeSpecificPart();
                } catch (NullPointerException unused) {
                    num = inCallService.getString(R.string.unknown);
                }
            }
        }
    }

    private static void resetAudioMode() {
        if (inCallService != null) {
            AudioManager audioManager = (AudioManager) inCallService.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(false);
            }
        }
    }


    public static void answerCall(Call call) {
        if (call != null) {
            call.answer(VideoProfile.STATE_AUDIO_ONLY);
        }
    }

    public static Call getCall() {
        if (!activeCalls.isEmpty()) {
            return activeCalls.get(activeCalls.size() - 1); // Return the last active call
        }
        return null; // No active calls
    }


    private final Call.Callback callCallback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
            if (state == Call.STATE_DISCONNECTED) {
                activeCalls.remove(call);
            }
            if (state != 2) {
                // CallManager.this.stopSound();
            }
            Intent intent = new Intent(CallManager.ACTION_CALL);
            intent.putExtra("data", state);
            CallManager.this.context.sendBroadcast(intent);

            if (state != 4) {
                if (state == 10 && CallManager.handler != null) {
                    CallManager.handler.removeCallbacks(CallManager.this.runnable);
                }
            } else if (CallManager.handler != null) {
                CallManager.handler.removeCallbacks(CallManager.this.runnable);
                CallManager.handler.post(CallManager.this.runnable);
            }
        }
    };


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (CallManager.handler != null) {
                CallManager.handler.postDelayed(this, 1000);
                String time2 = getTime(CallManager.time);
                Intent intent = new Intent(CallManager.ACTION_TIME);
                intent.putExtra("time", time2);
                CallManager.this.context.sendBroadcast(intent);
                CallManager.time++;
            }
        }
    };

    public static String getTime(int i) {
        int i2 = i / 60;
        int i3 = i % 60;
        StringBuilder sb = new StringBuilder();
        if (i2 < 10) {
            sb.append("0");
        }
        sb.append(i2);
        sb.append(":");
        if (i3 < 10) {
            sb.append("0");
        }
        sb.append(i3);
        return sb.toString();
    }

}
