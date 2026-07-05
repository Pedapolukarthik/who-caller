package com.androplaza.whocaller.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telecom.Call;
import android.telecom.InCallService;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.androplaza.whocaller.R;
import com.androplaza.whocaller.activities.CallActivity;
import com.androplaza.whocaller.helpers.CallManager;
import com.androplaza.whocaller.helpers.NotificationHelper;
import com.androplaza.whocaller.helpers.RingerManager;

import java.util.ArrayList;
import java.util.List;

public class CallService extends InCallService {

    public static final String END = "end";
    private CallManager callManager;
    private List<Call> activeCalls = new ArrayList<>(); // List to store active calls
    private RingerManager ringerManager;

    @Override
    public void onCreate() {
        super.onCreate();
        ringerManager = RingerManager.getInstance(this);
    }

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Log.d("CallService", "onCallAdded");
        CallManager.inCallService = this;
        if (call != null) {
            activeCalls.add(call); // Add the new call to the list
            call.registerCallback(callCallback); // Register callback for state changes

            String number;
            try {
                number = call.getDetails().getHandle().getSchemeSpecificPart();
            } catch (NullPointerException unused) {
                number = getString(R.string.unknown);
            }

            // Get SIM ID
            int simId = getSimIdFromCall(this, call);
            Log.d("CallService", "Incoming Call SIM ID: " + simId);

            CallManager.num = number;
            CallManager.handler = new Handler();
            callManager = new CallManager(this);
            callManager.setCall(call);
            TelecomAdapter.getInstance().setInCallService(this);

            Log.d("CallService", "call != null");

            // Note: Ringer will be started in onStateChanged callback to avoid double ringing

            Intent intent = new Intent(this, CallActivity.class);
            intent.putExtra("num", number);
            intent.putExtra(NotificationCompat.CATEGORY_STATUS, call.getState());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
                           Intent.FLAG_ACTIVITY_CLEAR_TOP | 
                           Intent.FLAG_ACTIVITY_SINGLE_TOP |
                           Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);

            String speakerBtnName = CallActivity.isSpeakerOn ? "Speaker Off" : "Speaker On";
            String muteBtnName = CallActivity.isMuted ? "Unmute" : "Mute";
            NotificationHelper.createIngoingCallNotification(this, call, "12:00:4", speakerBtnName, muteBtnName);
        }
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        activeCalls.remove(call); // Remove the call from the list
        
        // Stop ringer when call is removed
        ringerManager.stopRinging();
        
        if (activeCalls.isEmpty()) {
            TelecomAdapter.getInstance().clearInCallService();
            stopForeground(true);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && callManager != null && END.equals(intent.getAction())) {
            callManager.hangUpCall();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    // Callback to listen for call state changes
    private final Call.Callback callCallback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
            Log.d("CallService", "Call state changed to: " + state);
            
            switch (state) {
                case Call.STATE_RINGING:
                    // Start ringer for incoming calls
                    ringerManager.startRinging();
                    break;
                case Call.STATE_ACTIVE:
                    // Set audio mode when call becomes active
                    if (callManager != null) {
                        callManager.setCallAudioMode();
                    }
                    // Stop ringer when call is answered
                    ringerManager.stopRinging();
                    break;
                case Call.STATE_DISCONNECTED:
                case Call.STATE_DISCONNECTING:
                    // Stop ringer when call is disconnected or disconnecting
                    ringerManager.stopRinging();
                    break;
            }
            
            if (state == Call.STATE_DISCONNECTED) {
                activeCalls.remove(call);
            }
        }
    };

    // Switch between active calls
    public void switchCall(Call callToHold, Call callToResume) {
        if (callToHold != null) callToHold.hold();
        if (callToResume != null) callToResume.unhold();
    }

    // Merge calls into a conference
    public void mergeCalls() {
        if (activeCalls.size() >= 2) {
            Call firstCall = activeCalls.get(0);
            Call secondCall = activeCalls.get(1);
            firstCall.conference(secondCall);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Ensure ringer is stopped when service is destroyed
        if (ringerManager != null) {
            ringerManager.stopRinging();
        }
    }

    @SuppressLint("MissingPermission")
    private int getSimIdFromCall(Context context, Call call) {
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        if (telecomManager == null || subscriptionManager == null) {
            return -1;
        }

        PhoneAccountHandle phoneAccountHandle = call.getDetails().getAccountHandle();
        if (phoneAccountHandle == null) {
            return -1;
        }


        String phoneAccountId = phoneAccountHandle.getId();

        // Get all active SIMs
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (subscriptionInfoList == null || subscriptionInfoList.isEmpty()) {
            return -1;
        }


        // Try matching SIM ID
        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
            if (phoneAccountId.startsWith(subscriptionInfo.getIccId())) {
                return subscriptionInfo.getSubscriptionId();
            }
        }


        return -1;
    }

}
