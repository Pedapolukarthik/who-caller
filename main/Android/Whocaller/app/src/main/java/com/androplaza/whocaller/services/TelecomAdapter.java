package com.androplaza.whocaller.services;

import android.media.AudioManager;
import android.os.Looper;
import android.telecom.InCallService;


public class TelecomAdapter {
    private static TelecomAdapter mInstance;
    private InCallService inCallService;

    public static TelecomAdapter getInstance() {
        if (Looper.getMainLooper().isCurrentThread()) {
            if (mInstance == null) {
                mInstance = new TelecomAdapter();
            }
            return mInstance;
        }
        throw new IllegalStateException();
    }

    public void setInCallService(InCallService inCallService) {
        this.inCallService = inCallService;
    }

    public void switchSpeaker(AudioManager audioManager) {
        try {
            if (!audioManager.isSpeakerphoneOn()) {
                setAudioRoute(8);
            } else {
                setAudioRoute(1);
            }
        } catch (Exception e) {
            while (true) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void setAudioRoute(int i) {
        InCallService inCallService = this.inCallService;
        if (inCallService != null) {
            inCallService.setAudioRoute(i);
        }
    }

    public void muteSpeaker(AudioManager audioManager) {
        audioManager.setMode(2);
        boolean isMicrophoneMute = audioManager.isMicrophoneMute();
        InCallService inCallService = this.inCallService;
        if (inCallService != null) {
            inCallService.setMuted(!isMicrophoneMute);
        }
    }

    
    public void clearInCallService() {
        this.inCallService = null;
    }
}
