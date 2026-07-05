/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.utils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.NumberParseException;

public class PhoneNumberUtils {
    private static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public static String toNationalFormat(String phoneNumber, String defaultRegion) {
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber, defaultRegion);

            if (phoneNumberUtil.isValidNumber(number)) {
                return phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            }
        } catch (NumberParseException e) {
            e.printStackTrace();
        }

        return null;
    }



}
