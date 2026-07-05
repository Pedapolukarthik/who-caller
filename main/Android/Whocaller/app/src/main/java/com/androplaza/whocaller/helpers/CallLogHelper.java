/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.helpers;

import static com.androplaza.whocaller.utils.Utils.getCallTypeIconResId;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.androplaza.whocaller.modal.CallLogItem;

import java.util.ArrayList;
import java.util.List;

public class CallLogHelper {

    public static List<CallLogItem> getCallLogsForNumber(Context context, String phoneNumber) {
        List<CallLogItem> callLogItems = new ArrayList<>();

        ContentResolver resolver = context.getContentResolver();
        String[] projection = {
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };

        String selection = CallLog.Calls.NUMBER + " = ?";
        String[] selectionArgs = {phoneNumber};

        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, CallLog.Calls.DATE + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                int callType = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                String callDate = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                String callDuration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));


                CallLogItem callLogItem = new CallLogItem(
                        name,
                        number,
                        callType,
                        callDate,
                        getCallTypeIconResId(callType),
                        callDuration
                );

                callLogItems.add(callLogItem);
            }
            cursor.close();
        }

        return callLogItems;
    }


}
