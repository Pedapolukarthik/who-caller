/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.helpers;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telecom.Call;
import android.media.RingtoneManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.androplaza.whocaller.R;
import com.androplaza.whocaller.activities.CallActivity;


public class NotificationHelper {

    public static int NOTIFICATION_ID = 834831;

    public static void createIngoingCallNotification(Context context, Call call, String callDuration, String speakerBtnTxt, String muteBtnTxt) {

        String callerPhoneNumber, callerName;

        if (call.getDetails().hasProperty(Call.Details.PROPERTY_CONFERENCE)) {
            callerPhoneNumber = "Conference";
            callerName = "Conference";
        } else {
            callerPhoneNumber = call.getDetails().getHandle().getSchemeSpecificPart();
            callerName = ContactsHelper.getContactNameFromLocal(callerPhoneNumber, context);
        }

        String CHANNEL_ID = "Hidden_Pirates_Phone_App";

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, "Ingoing Call Notification", NotificationManager.IMPORTANCE_DEFAULT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Enable sound for call notifications
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE), null);
            channel.enableVibration(true);
        }

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(channel);
        }


        Intent ingoingCallIntent = new Intent(context, CallActivity.class);
        ingoingCallIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent ingoingCallPendingIntent;

        ingoingCallPendingIntent = PendingIntent.getActivity(context, 0, ingoingCallIntent, PendingIntent.FLAG_MUTABLE);


      //  Intent endCallIntent = new Intent(context, ActionReceiver.class);
       // endCallIntent.putExtra("endCall", "YES");

        PendingIntent endCallPendingIntent;

       // endCallPendingIntent = PendingIntent.getBroadcast(context, 1, endCallIntent, PendingIntent.FLAG_MUTABLE);


        //Intent speakerCallIntent = new Intent(context, ActionReceiver.class);
        //speakerCallIntent.putExtra("speakerCall", "YES");

        PendingIntent speakerCallPendingIntent;

        //speakerCallPendingIntent = PendingIntent.getBroadcast(context, 2, speakerCallIntent, PendingIntent.FLAG_MUTABLE);


       // Intent muteCallIntent = new Intent(context, ActionReceiver.class);
        //muteCallIntent.putExtra("muteCall", "YES");

        PendingIntent muteCallPendingIntent;

       // muteCallPendingIntent = PendingIntent.getBroadcast(context, 3, muteCallIntent, PendingIntent.FLAG_MUTABLE);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setOngoing(true);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setContentIntent(ingoingCallPendingIntent);
        builder.setFullScreenIntent(ingoingCallPendingIntent, true);
        builder.setSmallIcon(R.drawable.ic_call_green);
        builder.setContentInfo(callDuration);
        builder.setOnlyAlertOnce(true);
        builder.setContentTitle(callerName);
        builder.setContentText(callerPhoneNumber);
        builder.setCategory(Notification.CATEGORY_CALL);
        builder.setChannelId(CHANNEL_ID);
       // builder.addAction(R.drawable.ic_call_end_red, "End Call", endCallPendingIntent);
      //  builder.addAction(R.drawable.ic_volume_up, speakerBtnTxt, speakerCallPendingIntent);
      //  builder.addAction(R.drawable.ic_volume_up, muteBtnTxt, muteCallPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    public static void createOutgoingNotification(Context context, Call call) {

        String callerPhoneNumber = call.getDetails().getHandle().getSchemeSpecificPart();
        String callerName = ContactsHelper.getContactNameFromLocal(callerPhoneNumber, context);

        String CHANNEL_ID = "Hidden_Pirates_Phone_App";

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, "Outgoing Call Notification", NotificationManager.IMPORTANCE_DEFAULT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Enable sound for outgoing call notifications
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE), null);
            channel.enableVibration(true);
        }

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(channel);
        }


        Intent outingCallIntent = new Intent(context, CallActivity.class);
        outingCallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        PendingIntent outgoingCallPendingIntent;

        outgoingCallPendingIntent = PendingIntent.getActivity(context, 0, outingCallIntent, PendingIntent.FLAG_MUTABLE);


       // Intent cancelCallIntent = new Intent(context, ActionReceiver.class);
       // cancelCallIntent.putExtra("cancelCall", "YES");

        PendingIntent pickUpCallYesPendingIntent;

        //pickUpCallYesPendingIntent = PendingIntent.getBroadcast(context, 1, cancelCallIntent, PendingIntent.FLAG_IMMUTABLE);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setOngoing(true);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setContentIntent(outgoingCallPendingIntent);
        builder.setFullScreenIntent(outgoingCallPendingIntent, true);
        builder.setSmallIcon(R.drawable.ic_call_green);
        builder.setContentTitle(callerName);
        builder.setContentText(callerPhoneNumber);
        builder.setCategory(Notification.CATEGORY_CALL);
        builder.setChannelId(CHANNEL_ID);
       // builder.addAction(R.drawable.ic_call_end_red, "Cancel", pickUpCallYesPendingIntent);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
