/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.utils;


import static com.androplaza.whocaller.tabsFragments.SavedTabFragment.phonenumber;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androplaza.whocaller.activities.ContactManageActivity;
import com.androplaza.whocaller.activities.EditContactActivity;
import com.google.android.gms.ads.AdSize;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import com.naliya.callerid.database.prefs.AdsPrefHelper;
import com.naliya.callerid.database.prefs.ProfilePrefHelper;
import com.naliya.callerid.database.prefs.SettingsPrefHelper;
import com.androplaza.whocaller.BuildConfig;
import com.androplaza.whocaller.R;
import com.androplaza.whocaller.activities.MainActivity;
import com.androplaza.whocaller.activities.RedirectActivity;
import com.androplaza.whocaller.api.ApiClient;
import com.androplaza.whocaller.api.ApiResponse;
import com.androplaza.whocaller.modal.Ads;
import com.androplaza.whocaller.modal.Contact;
import com.androplaza.whocaller.modal.ResponseWrapper;
import com.androplaza.whocaller.modal.Settings;
import com.androplaza.whocaller.modal.UserProfile;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Utils {
    public static String TAG = "Utils";
    private static final int REQUEST_CALL_PERMISSION = 1;


    public static AdSize getAdSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }


    public static void shareApp(Activity activity, String title) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }


    public static void rateApp(Activity activity) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
    }

    public static void saveSettings(SettingsPrefHelper sharedPref, Settings settings) {
        sharedPref.saveSettings(
                settings.app_new_version,
                settings.app_redirect_url,
                settings.app_update_desc,
                settings.app_description,
                settings.app_email,
                settings.app_author,
                settings.app_contact,
                settings.app_website,
                settings.app_developed_by,
                settings.app_update_status == 1,
                settings.isMaintenance == 1,
                settings.app_status == 1,
                settings.privacy_policy,
                settings.more_apps_url


        );
    }

    public static void saveAds(AdsPrefHelper adsPref, Ads ads) {
        adsPref.saveAds(
                ads.ad_status == 1,
                ads.main_ads,
                ads.backup_ads,
                ads.admob_publisher_id,
                ads.admob_banner_unit_id,
                ads.admob_interstitial_unit_id,
                ads.admob_native_unit_id,
                ads.unity_game_id,
                ads.unity_banner_placement_id,
                ads.unity_interstitial_placement_id
        );
    }

    public static List<android.telecom.Call> callList = new ArrayList<>();

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawCircle((float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2), (float) (bitmap.getWidth() / 2), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }
    public static void getSettingsData(Context context) {
        SettingsPrefHelper sharedPref = new SettingsPrefHelper(context);
        ProfilePrefHelper profilePrefHelper = new ProfilePrefHelper(context);
        new Handler().postDelayed(() -> {
            ApiClient.ApiService apiService = ApiClient.getClient().create(ApiClient.ApiService.class);
            Call<ApiResponse> call = apiService.getApiData();

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse apiResponse = response.body();
                        List<Settings> settings = apiResponse.getSettings();
                        List<Ads> ads = apiResponse.getAds();

                        AdsPrefHelper adsPref = new AdsPrefHelper(context);


                        if (!ads.isEmpty()) {
                            Ads ads1 = ads.get(0);
                            Utils.saveAds(adsPref, ads1);
                        }

                        if (!settings.isEmpty()) {
                            Settings settings1 = settings.get(0);
                            Utils.saveSettings(sharedPref, settings1);
                        }

                        if (sharedPref.getApp_status()) {
                            profilePrefHelper.setIsSignUser(true);
                        }

                    } else {
                        Log.e("API Error", "Response unsuccessful or empty body");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    Log.e("API Error", t.getMessage(), t);
                }
            });
        }, 100);
    }

    public static void openContactEditPage(Context context, String lookupKey) {
        if (lookupKey != null && !lookupKey.isEmpty()) {
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            Uri contactUri = ContactsContract.Contacts.lookupContact(context.getContentResolver(), lookupUri);

            if (contactUri != null) {
                Log.d("ContactEdit", "Contact URI: " + contactUri);
                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                editIntent.setData(contactUri);
                editIntent.putExtra("finishActivityOnSaveCompleted", true);

                if (editIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(editIntent);
                    Log.d("ContactEdit", "Starting contact edit activity.");
                } else {
                    Intent customEditIntent = new Intent(context, EditContactActivity.class);
                    customEditIntent.putExtra("lookupKey", lookupKey);
                    context.startActivity(customEditIntent);
                }
            } else {
                Toast.makeText(context, "Contact not found", Toast.LENGTH_SHORT).show();
                Log.e("ContactEdit", "Contact not found.");
            }
        } else {
            Toast.makeText(context, "cant edit at that time", Toast.LENGTH_SHORT).show();
            Log.e("ContactEdit", "Invalid lookup key.");
        }
    }

    public static void openContactCreatePage(Context context, String phoneNumber, String name) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Intent intents = new Intent(context, ContactManageActivity.class);
            intents.putExtra("name", name);
            intents.putExtra("phone", phoneNumber);
            context.startActivity(intents);
        }
    }
    public static void getContactDataDetails(String phone, ContactDataCallback callback) {
        ApiClient.ApiService apiService = ApiClient.getClient().create(ApiClient.ApiService.class);

        Call<ResponseWrapper<Object>> call = apiService.getContactData(phone);
        call.enqueue(new Callback<ResponseWrapper<Object>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<Object>> call, @NonNull Response<ResponseWrapper<Object>> response) {
                if (response.isSuccessful()) {
                    ResponseWrapper<Object> responseWrapper = response.body();
                    if (responseWrapper != null) {
                        String type = responseWrapper.getType();
                        if ("profile".equals(type)) {
                            UserProfile profile = new Gson().fromJson(new Gson().toJson(responseWrapper.getModel()), UserProfile.class);
                            callback.onSuccess(profile);
                        } else if ("contacts".equals(type)) {
                            Contact contact = new Gson().fromJson(new Gson().toJson(responseWrapper.getModel()), Contact.class);
                            callback.onSuccess(contact);
                        } else {
                            callback.onError("Unknown type: " + type);
                        }
                    } else {
                        callback.onError("No data found");
                    }
                } else {
                    callback.onError("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<Object>> call, @NonNull Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public interface ContactDataCallback {
        void onSuccess(Object data); // Change the parameter type to Object

        void onError(String errorMessage);
    }

    public static void getSearchContactDataDetails(int id, String type, SearchContactDataCallback callback) {
        ApiClient.ApiService apiService = ApiClient.getClient().create(ApiClient.ApiService.class);

        Call<ResponseWrapper<Object>> call = apiService.getSearchContactData(id, type);
        call.enqueue(new Callback<ResponseWrapper<Object>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<Object>> call, @NonNull Response<ResponseWrapper<Object>> response) {
                if (response.isSuccessful()) {
                    ResponseWrapper<Object> responseWrapper = response.body();

                    if (responseWrapper != null) {
                        if ("UserProfile".equals(responseWrapper.getType())) {
                            UserProfile profile = new Gson().fromJson(new Gson().toJson(responseWrapper.getModel()), UserProfile.class);
                            callback.onSuccess(profile);
                        } else if ("Contact".equals(responseWrapper.getType())) {
                            Contact contact = new Gson().fromJson(new Gson().toJson(responseWrapper.getModel()), Contact.class);
                            callback.onSuccess(contact);
                        } else {
                            callback.onError("Unknown type: " + responseWrapper.getType());
                        }
                    } else {
                        callback.onError("No data found");
                    }
                } else {
                    callback.onError("Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<Object>> call, @NonNull Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public interface SearchContactDataCallback {
        void onSuccess(Object data);

        void onError(String errorMessage);
    }

    public static String toTextCase(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }

        return titleCase.toString();
    }

    public static boolean isValidURL(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static void shareContact(Context context, String name, String number) {
        String contactInfo = "Name: " + name + "\nNumber: " + number;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, contactInfo);
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, "Share contact via"));
    }

    public static void copyToClipboard(Context context, String text, String label) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }




    public static void reDirectMainActivity(Context context) {
        SettingsPrefHelper sharedPref = new SettingsPrefHelper(context);
        if (sharedPref.getApp_status() && !sharedPref.getMaintenance() && !sharedPref.getAppUpdateStatus()) {
            context.startActivity(new Intent(context, MainActivity.class));
        } else if (sharedPref.getMaintenance()) {
            Intent intent = new Intent(context, RedirectActivity.class);
            intent.putExtra("isUpdate", false);
            context.startActivity(intent);
        } else if (!sharedPref.getMaintenance() && sharedPref.getAppUpdateStatus()) {
            Intent intent = new Intent(context, RedirectActivity.class);
            intent.putExtra("isUpdate", true);
            context.startActivity(intent);
        }

    }


    public static void getProfileData(Context context, UserProfile userProfile) {
        ApiClient.ApiService apiService = ApiClient.getClient().create(ApiClient.ApiService.class);
        Call<UserProfile.UserProfileResponse> call = apiService.getProfileDetails(userProfile);
        call.enqueue(new Callback<UserProfile.UserProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile.UserProfileResponse> call, @NonNull Response<UserProfile.UserProfileResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    UserProfile.UserProfileResponse userProfileResponse = response.body();
                    if ("success".equals(userProfileResponse.getStatus())) {
                        UserProfile user = userProfileResponse.getData();
                        setUserdata(context, user);

                    } else {
                        setUserdata(context, userProfile);
                        Log.e("setProfileData", "Error: " + userProfileResponse.getMessage());
                    }
                } else {
                    setUserdata(context, userProfile);
                    Log.e("setProfileData", "Response unsuccessful or empty");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile.UserProfileResponse> call, @NonNull Throwable t) {
                setUserdata(context, userProfile);
                Log.e("setProfileData", "Request failed", t);
            }
        });

    }

    public static void setUserdata(Context context, UserProfile user) {
        ProfilePrefHelper profilePrefHelper = new ProfilePrefHelper(context);

        String FirstName = (user.getFirstName());
        String LastName = (user.getLastName());
        String Phone = (user.getPhone());
        String Email = (user.getEmail());
        String ImageUrl = "";

        if (user.getImgUrl() != null) {
            if (user.getImgUrl().contains("public/")) {
                ImageUrl = user.getImgUrl().replace("public/", "");
            } else {
                ImageUrl = (user.getImgUrl());
            }
        }

        profilePrefHelper.saveUserProfile(FirstName, LastName, Phone, Email, ImageUrl);
        profilePrefHelper.setIsSignUser(true);
        Utils.reDirectMainActivity(context);
    }


    public static void postContacts(List<Contact> contactList, String phone_number) {
        phonenumber = phone_number;
        retrieveContactsFromBackend(new OnContactsRetrievedListener() {
            @Override
            public void onContactsRetrieved(List<Contact> backendContacts) {
                List<Contact> contactsToSave = filterContact(contactList, backendContacts);

                if (contactsToSave.isEmpty()) {
                    Log.d(TAG, "All contacts are already saved");
                    return;
                }


                ApiClient.ContactListRequest contactListRequest = new ApiClient.ContactListRequest(contactsToSave, phonenumber);


                ApiClient.ApiService apiService = ApiClient.getClient().create(ApiClient.ApiService.class);
                Call<Void> call = apiService.postContacts(contactListRequest);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Contacts posted successfully");
                        } else {
                            Log.e(TAG, "Failed to post contact");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.e(TAG, "Failed to post contact: " + t.getMessage());
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to retrieve contacts from backend: " + errorMessage);
            }
        });
    }

    public static void postContact(List<Contact> contactList, String phone_number) {


        ApiClient.ContactListRequest contactListRequest = new ApiClient.ContactListRequest(contactList, phone_number);


        ApiClient.ApiService apiService = ApiClient.getClient().create(ApiClient.ApiService.class);
        Call<Void> call = apiService.postContact(contactListRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Contacts posted successfully");
                } else {
                    Log.e(TAG, "Failed to post contact");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to post contact: " + t.getMessage());
            }
        });
    }


    public static void retrieveContactsFromBackend(OnContactsRetrievedListener listener) {
        ApiClient.ApiService apiService = ApiClient.getClient().create(ApiClient.ApiService.class);
        Call<List<Contact>> call = apiService.getContacts(phonenumber);
        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(@NonNull Call<List<Contact>> call, @NonNull Response<List<Contact>> response) {
                if (response.isSuccessful()) {
                    List<Contact> backendContacts = response.body();
                    listener.onContactsRetrieved(backendContacts);
                } else {
                    listener.onError("Failed to retrieve contacts from backend: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Contact>> call, @NonNull Throwable t) {
                listener.onError("Failed to retrieve contacts from backend: " + t.getMessage());
            }
        });
    }

    public interface OnContactsRetrievedListener {
        void onContactsRetrieved(List<Contact> backendContacts);

        void onError(String errorMessage);

    }

    private static List<Contact> filterContact(List<Contact> deviceContacts, List<Contact> backendContacts) {
        Set<String> deviceContactNumbers = new HashSet<>();
        for (Contact contact : deviceContacts) {
            deviceContactNumbers.add(contact.getPhoneNumber());
        }

        Set<String> backendContactNumbers = new HashSet<>();
        for (Contact contact : backendContacts) {
            backendContactNumbers.add(contact.getPhoneNumber());
        }


        List<Contact> filteredContacts = new ArrayList<>();
        for (Contact contact : deviceContacts) {
            if (!backendContactNumbers.contains(contact.getPhoneNumber())) {
                filteredContacts.add(contact);
            }
        }

        return filteredContacts;
    }


    public static boolean isContactStarred(String phoneNumbe, Context context) {
        boolean isStarred = false;

        // Query the contacts database to check if the phone number is starred
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumbe));
        Cursor cursor = context.getContentResolver().query(
                uri,
                new String[]{ContactsContract.PhoneLookup.STARRED},
                null,
                null,
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int starred = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.STARRED));
                isStarred = (starred == 1);
            }
            cursor.close();
        }

        return isStarred;
    }

    public static boolean isPhoneNumberSaved(String phoneNumber, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = contentResolver.query(uri,
                new String[]{ContactsContract.PhoneLookup._ID}, null, null, null);

        boolean isSaved = (cursor != null && cursor.getCount() > 0);

        if (cursor != null) {
            cursor.close();
        }

        return isSaved;
    }


    public static List<Contact> loadFavoriteContacts(Context context) {
        List<Contact> contacts = new ArrayList<>();

        // Define the selection criteria
        String selection = ContactsContract.Contacts.STARRED + "=?";
        String[] selectionArgs = new String[]{"1"};

        // Query the contacts database with the selection criteria
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(new Contact(name, phone, false, false, null, null, null, null, "contacts"));

            }
            cursor.close();
        }

        return contacts;
    }

    public static class ContactLoaderTask {

        private Context context;
        private OnContactsLoadedListener listener;
        private RelativeLayout progressBar;

        public ContactLoaderTask(Context context, RelativeLayout progressBar, OnContactsLoadedListener listener) {
            this.context = context;
            this.listener = listener;
            this.progressBar = progressBar;
        }

        public void execute() {
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                List<Contact> contacts = doInBackground();
                new Handler(Looper.getMainLooper()).post(() -> onPostExecute(contacts));
                executor.shutdown();
            });
        }

        private List<Contact> doInBackground() {
            List<Contact> contacts = new ArrayList<>();
            Set<String> phoneNumbersSet = new LinkedHashSet<>();
            Cursor cursor = null;

            try {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    return contacts;
                }

                // Define the columns to fetch to improve performance
                String[] projection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };

                cursor = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        projection, // Fetch only the necessary columns
                        null, null, null);

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        // If phone number is null or empty, skip this contact
                        if (phone == null || phone.isEmpty()) continue;

                        // Normalize the phone number by removing spaces and other formatting characters
                        String normalizedPhone = normalizePhoneNumber(phone);

                        // Add the contact only if the phone number is not already in the set
                        if (!phoneNumbersSet.contains(normalizedPhone)) {
                            phoneNumbersSet.add(normalizedPhone); // Add to the set
                            contacts.add(new Contact(name != null ? name : "Unknown", normalizedPhone, false, false, null, null, null, null, "contacts"));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ContactLoaderTask", "Error loading contacts", e);
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

            // Sort contacts alphabetically by name
            Collections.sort(contacts, new Comparator<Contact>() {
                @Override
                public int compare(Contact c1, Contact c2) {
                    return c1.getName().compareToIgnoreCase(c2.getName());
                }
            });

            return contacts;
        }

        private String normalizePhoneNumber(String phone) {
            if (phone == null) return ""; // Handle null input
            // Remove all non-numeric characters except for "+"
            String normalized = phone.replaceAll("[^\\d+]", "");
            // Remove spaces specifically
            return normalized.replaceAll("\\s+", "");
        }


        private void onPostExecute(List<Contact> contacts) {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }

            if (listener != null) {
                listener.onContactsLoaded(contacts);
            }
        }

        public interface OnContactsLoadedListener {
            void onContactsLoaded(List<Contact> contacts);
        }
    }


    public static String getLookupKeyFromPhoneNumber(String phoneNumber, Context context) {
        String lookupKey = null;

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.LOOKUP_KEY};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                lookupKey = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.LOOKUP_KEY));
            }
            cursor.close();
        }

        return lookupKey;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                return networkCapabilities != null &&
                        (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            }
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public static void makeCall(String inputNumber, Context context) {
        Log.e("MakeCall", "Dialing number: " + inputNumber);

        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        if (subscriptionManager != null) {
            List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            if (subscriptionInfoList != null && subscriptionInfoList.size() > 1) {
                // Check if user has a saved SIM preference
                int savedSimId = getSavedSimPreference(context, inputNumber);

                // Log.e("makeCall","simId: " + simId);
                if (savedSimId != -1) {
                    getCall(inputNumber, context, savedSimId);
                } else {
                    // Show SIM selection dialog
                    showSimSelectionDialog(inputNumber, context);
                }
            } else if (subscriptionInfoList != null && subscriptionInfoList.size() == 1) {
                // Only one SIM available, call directly
                int simId = subscriptionInfoList.get(0).getSubscriptionId();
                getCall(inputNumber, context, simId);
            } else {
                // No SIM detected, show an error message
                Toast.makeText(context, "No SIM card detected.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Unable to get SIM information", Toast.LENGTH_SHORT).show();
        }
    }


    // Save preferred SIM for a specific number
    private static void saveSimPreference(Context context, String phoneNumber, int simId) {
        SharedPreferences preferences = context.getSharedPreferences("SIM_PREFERENCES", Context.MODE_PRIVATE);
        preferences.edit().putInt(phoneNumber, simId).apply();
    }

    // Get saved SIM preference
    private static int getSavedSimPreference(Context context, String phoneNumber) {
        SharedPreferences preferences = context.getSharedPreferences("SIM_PREFERENCES", Context.MODE_PRIVATE);
        return preferences.getInt(phoneNumber, -1);
    }

    private static void showSimSelectionDialog(String inputNumber, Context context) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        @SuppressLint("MissingPermission")
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

        if (subscriptionInfoList == null || subscriptionInfoList.isEmpty()) {
            Toast.makeText(context, "No SIM cards available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create custom Dialog
        Dialog dialog = new Dialog(context, R.style.CustomDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sim_selection);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false); // Prevent accidental dismiss

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroupSim);
        CheckBox checkBoxSavePreference = dialog.findViewById(R.id.checkBoxSavePreference);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnCall = dialog.findViewById(R.id.btnCall);

        int[] simIds = new int[subscriptionInfoList.size()];
        for (int i = 0; i < subscriptionInfoList.size(); i++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(subscriptionInfoList.get(i).getDisplayName().toString());
            radioButton.setId(i);
            radioGroup.addView(radioButton);
            simIds[i] = subscriptionInfoList.get(i).getSubscriptionId();
        }

        // Cancel button action
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Call button action
        btnCall.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                int selectedSimId = simIds[selectedId];

                if (checkBoxSavePreference.isChecked()) {
                    saveSimPreference(context, inputNumber, selectedSimId);
                }

                getCall(inputNumber, context, selectedSimId);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Please select a SIM", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the dialog
        dialog.show();
    }

    public static void getCall(String phoneNumber, Context context, int simId) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        int simSlot = getSlotForSimId(context, simId);
        PhoneAccountHandle handle = getPhoneAccountHandle(context, simId);

        if (simSlot != -1 && handle != null) {
            intent.putExtra("com.android.phone.extra.slot", simSlot);
            intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, handle);
            context.startActivity(intent);
        } else {
            Log.e("CallService", "Invalid SIM ID or PhoneAccountHandle!");
        }

    }


    private static int getSlotForSimId(Context context, int simId) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (subscriptionManager != null) {
            List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            if (subscriptionInfoList != null) {
                for (SubscriptionInfo info : subscriptionInfoList) {
                    if (info.getSubscriptionId() == simId) {
                        return info.getSimSlotIndex(); // Returns 0 for SIM1, 1 for SIM2
                    }
                }
            }
        }
        return -1; // Default to unknown slot
    }

    private static PhoneAccountHandle getPhoneAccountHandle(Context context, int simId) {
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        if (telecomManager != null && subscriptionManager != null) {
            List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            List<PhoneAccountHandle> phoneAccounts = telecomManager.getCallCapablePhoneAccounts();

            if (subscriptionInfoList != null && phoneAccounts != null) {
                for (SubscriptionInfo info : subscriptionInfoList) {
                    if (info.getSubscriptionId() == simId) {
                        String iccId = info.getIccId();  // Get ICC ID for the SIM
                        Log.d("CallService", "Matching ICC ID: " + iccId);

                        for (PhoneAccountHandle handle : phoneAccounts) {
                            if (handle.getId().contains(iccId)) {
                                Log.d("CallService", "Matched PhoneAccountHandle: " + handle.getId());
                                return handle;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }



    public static int getCallTypeIconResId(int callType) {
        switch (callType) {
            case CallLog.Calls.INCOMING_TYPE:
                return R.drawable.ic_incoming_call;
            case CallLog.Calls.OUTGOING_TYPE:
                return R.drawable.ic_outgoing_call;
            case CallLog.Calls.MISSED_TYPE:
                return R.drawable.ic_missed_call;
            case CallLog.Calls.VOICEMAIL_TYPE:
                return R.drawable.ic_voicemail;
            case CallLog.Calls.REJECTED_TYPE:
                return R.drawable.ic_rejected_call;
            case CallLog.Calls.BLOCKED_TYPE:
                return R.drawable.ic_blocked_call;
            case CallLog.Calls.ANSWERED_EXTERNALLY_TYPE:
                return R.drawable.ic_answered_externally;
            default:
                return R.drawable.ic_unknown_call;
        }
    }

    public static String formatDate(String dateString) {
        long milliseconds = Long.parseLong(dateString);
        Calendar callDate = Calendar.getInstance();
        callDate.setTimeInMillis(milliseconds);

        Calendar now = Calendar.getInstance();

        if (now.get(Calendar.YEAR) == callDate.get(Calendar.YEAR)) {
            if (now.get(Calendar.DAY_OF_YEAR) == callDate.get(Calendar.DAY_OF_YEAR)) {
                // Today: Show time only with AM/PM
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                return timeFormat.format(callDate.getTime());
            } else if (now.get(Calendar.DAY_OF_YEAR) - callDate.get(Calendar.DAY_OF_YEAR) == 1) {
                // Yesterday: Show "Yesterday" with month name and day number
                SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
                return monthDayFormat.format(callDate.getTime());
            } else if (now.get(Calendar.DAY_OF_YEAR) - callDate.get(Calendar.DAY_OF_YEAR) < 7) {
                // This week: Show day name
                SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
                return monthDayFormat.format(callDate.getTime());
            } else {
                // This year: Show month name and day number
                SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
                return monthDayFormat.format(callDate.getTime());
            }
        } else {
            // Other years: Show month name, day number, and year
            SimpleDateFormat fullDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return fullDateFormat.format(callDate.getTime());
        }
    }


    public static Bitmap generateAvatar(String name) {
        int size = 100;
        int backgroundColor = getLightColor();

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(backgroundColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);

        int darkenedColor = getDarkenedColor(backgroundColor);
        paint.setColor(darkenedColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(45);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(String.valueOf(name.charAt(0)).toUpperCase(), size / 2, size / 2 + 15, paint);

        return bitmap;
    }

    public static int getDarkenedColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.6f;
        return Color.HSVToColor(hsv);
    }

    public static int getLightColor() {
        Random random = new Random();
        int red = (random.nextInt(156) + 100);
        int green = (random.nextInt(156) + 100);
        int blue = (random.nextInt(156) + 100);
        return Color.rgb(red, green, blue);
    }


    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches(".*[a-zA-Z]+.*");
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        String regex = "^[+]?[0-9]{10,15}$";
        return phoneNumber.matches(regex);
    }

    public static String getCallTypeString(int callType) {
        switch (callType) {
            case CallLog.Calls.INCOMING_TYPE:
                return "Incoming";
            case CallLog.Calls.OUTGOING_TYPE:
                return "Outgoing";
            case CallLog.Calls.MISSED_TYPE:
                return "Missed";
            case CallLog.Calls.REJECTED_TYPE:
                return "Rejected";
            default:
                return "Rejected";
        }
    }

    public static void lookupCarrier(Context context, String phoneNumber, CarrierLookupCallback callback) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://lookups.twilio.com/v1/PhoneNumbers/" + phoneNumber + "?Type=carrier";

        Request request = new Request.Builder().url(url).addHeader("Authorization", okhttp3.Credentials.basic(context.getString(R.string.twilid_id), context.getString(R.string.twilid_auth_token))).build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONObject carrier = jsonObject.getJSONObject("carrier");
                        String carrierName = carrier.getString("name");
                        String countryCode = jsonObject.getString("country_code");
                        callback.onSuccess(carrierName, countryCode);
                    } catch (Exception e) {
                        callback.onFailure(e);
                    }
                } else {
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }
        });
    }

    public interface CarrierLookupCallback {
        void onSuccess(String carrierName, String countryCode);

        void onFailure(Exception e);
    }


    public static String getCountryNameFromPhoneNumber(Context context,String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(getFormatNumber(context,phoneNumber), "US");
            String regionCode = phoneUtil.getRegionCodeForNumber(number);
            if (regionCode != null) {
                Locale locale = new Locale("", regionCode);
                return locale.getDisplayCountry();
            }
        } catch (NumberParseException e) {
            Log.e("PhoneNumber", "Error parsing number: " + e.getMessage());
        }
        return null;

    }


    public static String getFormatNumber(Context context,String phoneNumber){

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String regionCodex = Utils.getRegionCode(context);

        Phonenumber.PhoneNumber phoneNumberr;
        String FormatPhoneNumber = phoneNumber.replace("*", "").replace("#", "");
        try {
            phoneNumberr = phoneNumberUtil.parse(FormatPhoneNumber, regionCodex);
        } catch (NumberParseException e) {
            throw new RuntimeException(e);
        }
        if (phoneNumberUtil.isValidNumber(phoneNumberr)) {
            FormatPhoneNumber = phoneNumberUtil.format(phoneNumberr, PhoneNumberUtil.PhoneNumberFormat.E164);
        }


        if (phoneNumber.equals(FormatPhoneNumber)) {
            if (FormatPhoneNumber.startsWith("0")) {
                FormatPhoneNumber = FormatPhoneNumber.substring(1);
                FormatPhoneNumber = "+" + FormatPhoneNumber;
            }
        }

        return FormatPhoneNumber;
    }

    public static String getRegionCode(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simCountryIso = telephonyManager.getSimCountryIso().toUpperCase();
        if (simCountryIso.isEmpty()) {
            simCountryIso = Locale.getDefault().getCountry();
        }
        return simCountryIso;
    }
    public static Bitmap getContactImage(Context context, String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.PHOTO_URI};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI));
                cursor.close();
                if (photoUri != null) {
                    try {
                        InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(photoUri));
                        return BitmapFactory.decodeStream(inputStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                cursor.close();
            }
        }
        return null;
    }

    public static void startExternalApplication(Context context, String url) {
        try {
            String[] results = url.split("package=");
            String packageName = results[1];
            boolean isAppInstalled = appInstalledOrNot(context, packageName);
            if (isAppInstalled) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setPackage(packageName);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
                Log.i(TAG, "Application is already installed.");
            } else {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                Log.i(TAG, "Application is not currently installed.");
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "error : " + e.getMessage());
        }
    }

    private static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "NameNotFoundException");
        }
        return false;
    }

    public static void loadHtml(Activity activity, WebView webView, String content, boolean injectAssets) {

        webView.setBackgroundColor(Color.TRANSPARENT);
        try {
            AssetManager assetManager = activity.getAssets();
            InputStream stream;
            stream = assetManager.open("html/light.html");


            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder startBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                startBuilder.append(line).append("\n");
            }
            String endBuilder = "</body></html>";
            if (injectAssets) {
                webView.loadDataWithBaseURL(null, startBuilder + content + endBuilder, "text/html", "UTF-8", null);
            } else {
                webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
