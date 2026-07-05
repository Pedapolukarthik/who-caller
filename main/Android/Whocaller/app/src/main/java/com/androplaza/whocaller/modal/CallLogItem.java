/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.modal;

import android.annotation.SuppressLint;

import java.util.concurrent.TimeUnit;

public class CallLogItem {
    private String name;
    private String phoneNumber;
    private int callType;
    private String callDate;
    private int callTypeIconResId;
    private String callDuration;


    public CallLogItem(String name, String phoneNumber, int callType, String callDate, int callTypeIconResId, String callDuration) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.callType = callType;
        this.callDate = callDate;
        this.callTypeIconResId = callTypeIconResId;
        this.callDuration = callDuration;

    }


    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getCallType() {
        return callType;
    }

    public String getCallDate() {
        return callDate;
    }

    public int getCallTypeIconResId() {
        return callTypeIconResId;
    }

    @SuppressLint("DefaultLocale")
    public String getFormattedCallDuration() {
        long durationInSeconds = Long.parseLong(callDuration);
        long hours = TimeUnit.SECONDS.toHours(durationInSeconds);
        long minutes = TimeUnit.SECONDS.toMinutes(durationInSeconds) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = durationInSeconds - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);

        if (hours > 0) {
            return String.format("%d hr %d min %d sec", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d min %d sec", minutes, seconds);
        } else {
            return String.format("%d sec", seconds);
        }
    }
}
