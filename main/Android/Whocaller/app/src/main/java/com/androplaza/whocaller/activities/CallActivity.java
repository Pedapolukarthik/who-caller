/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.activities;


import static com.androplaza.whocaller.helpers.CallManager.num;
import static com.androplaza.whocaller.helpers.ContactsHelper.getContactNameFromLocal;
import static com.androplaza.whocaller.utils.Utils.getContactImage;
import static com.androplaza.whocaller.utils.Utils.isContactStarred;
import static com.androplaza.whocaller.utils.Utils.isPhoneNumberSaved;
import static com.androplaza.whocaller.utils.Utils.isValidName;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.telecom.Call;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.androplaza.whocaller.R;
import com.androplaza.whocaller.database.sqlite.BlockCallerDbHelper;
import com.androplaza.whocaller.database.sqlite.ContactsDataDb;
import com.androplaza.whocaller.databinding.ActivityCallBinding;
import com.androplaza.whocaller.helpers.CallManager;
import com.androplaza.whocaller.helpers.NotificationHelper;
import com.androplaza.whocaller.helpers.RingerManager;
import com.androplaza.whocaller.modal.Contact;
import com.androplaza.whocaller.modal.UserProfile;
import com.androplaza.whocaller.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naliya.callerid.database.prefs.SettingsPrefHelper;

public class CallActivity extends AppCompatActivity {

    LinearLayout endCallBtn;

    @SuppressLint("StaticFieldLeak")
    public static Button recordBtn;
    ImageView mergeCallBtn;

    @SuppressLint("StaticFieldLeak")
    public static ImageView muteBtn, speakerBtn, holdBtn, addCallBtn;

    private float dY;
    LinearLayout draggableButton;
    ImageView arrowUp, arrowDown, actionBtn;
    private float topLimit;
    private float bottomLimit;
    private boolean isButtonDragged = false;
    private float initialY;
    ObjectAnimator shinyAnimator;

    Button btn0, btn01, btn02, btn03, btn04, btn05, btn06, btn07, btn08, btn09, btnStar, btnHash;

    BottomSheetDialog keypadDialog;
    String keypadDialogTextViewText = "";

    @SuppressLint("StaticFieldLeak")
    public static TextView callerNameTV, callerPhoneNumberTV, callDurationTV, callingStatusTV;

    @SuppressLint("StaticFieldLeak")
    public static TextView incomingCallerPhoneNumberTV, incomingCallerNameTV, ringingStatusTV;

    RelativeLayout inProgressCallRLView, incomingRLView;

    public static boolean isMuted, isSpeakerOn, isCallOnHold;

    public static String PHONE_NUMBER, CALLER_NAME;

    public static String muteBtnName = "Mute", speakerBtnName = "Speaker On";

    ContactsDataDb contactsDataDb;
    Window window;
    ActivityCallBinding binding;
    SettingsPrefHelper settingsPrefHelper;
    BlockCallerDbHelper blockCallerDbHelper;

    Boolean CallAnswerd = false;
    private CallManager callManager;
    private RingerManager ringerManager;


    public void callStatus(int i) {
        if (i != 1) {
            //calling();
            Log.d("CallActivity", "callStatus if");
            if (i == 2) {
                //updateLayout(true);
                Log.d("CallActivity", "callStatus i == 2");
                calling();
                return;
            } else if (i == 4) {
                Log.d("CallActivity", "callStatus i == 4");
                // Stop ringer when call is answered
                if (ringerManager != null) {
                    ringerManager.stopRinging();
                }
                callAnswerd();
                return;
            } else if (i == 7) {
                Log.d("CallActivity", "callStatus i == 7");
                // Stop ringer when call is disconnected
                if (ringerManager != null) {
                    ringerManager.stopRinging();
                }
                if(CallManager.getActiveCalls().isEmpty()){
                    callEnd();
                }else {
                    Log.d("callStatus", "callStatus answerd");
                    callAnswerd();
                }
                return;
            } else if (i != 9) {
                if (i == 10) {
                    Log.d("CallActivity", "callStatus i == 10");
                    // Stop ringer when call is disconnecting
                    if (ringerManager != null) {
                        ringerManager.stopRinging();
                    }
                    return;
                }
                Log.d("CallActivity", "callStatus without i != 9");
                return;
            }
        }

        Log.d("CallActivity", "callStatus without");
        callAnswerd();
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("CallActivity", "CallActivity onReceive");

            String action = intent.getAction();
            Log.d("CallActivity", "action: " + action);
            if (action == null) {
                return;
            }
            if (action.equals(CallManager.ACTION_CALL)) {
                Log.d("CallActivity", "CallActivity getIntExtra: " + intent.getIntExtra("data", -1));
                // callStatus();
                callStatus(intent.getIntExtra("data", -1));

                return;
            }

            String stringExtra = intent.getStringExtra("time");
            if (stringExtra == null || stringExtra.isEmpty()) {
                stringExtra = "00:00";
            }

            setTime(stringExtra);
        }
    };


    @SuppressLint("UseCompatLoadingForDrawables")
    private void callAnswerd() {
        Log.d("CallActivity", "CallActivity callAnswerd");
        inProgressCallRLView.setVisibility(View.VISIBLE);
        incomingRLView.setVisibility(View.GONE);


        PHONE_NUMBER = num;

        Log.d("CallActivity", "CallActivity callAnswerd num: " + PHONE_NUMBER);

        CALLER_NAME = getContactNameFromLocal(PHONE_NUMBER, CallActivity.this);
        Contact contact = contactsDataDb.getContactByPhoneNumber(PHONE_NUMBER);

        if (contact != null) {
            CALLER_NAME = contact.getName();
        } else {
            if (Utils.isNetworkAvailable(CallActivity.this)) {
                getContactData();
            }

        }
        callerNameTV.setText(CALLER_NAME);

        if (isContactStarred(PHONE_NUMBER, CallActivity.this)) {
            binding.favorite2.setImageResource(R.drawable.favorite_fill);
            binding.favorite2.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(CallActivity.this, R.color.white)));
        }


        if (isValidName(CALLER_NAME)) {
            Bitmap contactImage = getContactImage(CallActivity.this, PHONE_NUMBER);
            if (contactImage != null) {
                binding.profilePic2.setImageBitmap(contactImage);
            } else {
                binding.profilePic2.setImageDrawable(getResources().getDrawable(R.drawable.ic_avatar, null));
            }
        } else {
            binding.profilePic2.setImageDrawable(getResources().getDrawable(R.drawable.ic_avatar, null));
        }


        if (contact != null) {
            if (contact.getCountryName() != null) {
                binding.countryName2.setVisibility(View.VISIBLE);
                binding.carriorName2.setVisibility(View.VISIBLE);
                binding.view2.setVisibility(View.VISIBLE);
                binding.countryName2.setText(contact.getCountryName());
                if (contact.getCarrierName() != null) {
                    binding.carriorName2.setText(contact.getCarrierName());
                }
            }else {
                binding.countryName2.setVisibility(View.VISIBLE);
                binding.countryName2.setText(Utils.getCountryNameFromPhoneNumber(this,PHONE_NUMBER));

            }

            if (contact.isWho()) {
                binding.whoProfile2.setVisibility(View.VISIBLE);
            }
            if (contact.getContactsBy() != null) {
                if (contact.getContactsBy().equals("whocaller") || !isPhoneNumberSaved(PHONE_NUMBER, CallActivity.this)) {
                    binding.whoLay2.setVisibility(View.VISIBLE);
                }
            }
            if (contact.isSpam()) {
                window.setStatusBarColor(getResources().getColor(R.color.red, null));
                binding.profilePic2.setImageDrawable(getResources().getDrawable(R.drawable.spam_circle, null));
                binding.inProgressCallRLView.setBackground(getDrawable(R.drawable.bg_red));
            }


        }

        binding.countryName2.setVisibility(View.VISIBLE);
        binding.countryName2.setText(Utils.getCountryNameFromPhoneNumber(this,PHONE_NUMBER));

        if (blockCallerDbHelper.isPhoneNumberBlocked(PHONE_NUMBER)) {
            window.setStatusBarColor(getResources().getColor(R.color.red, null));
            binding.profilePic2.setImageDrawable(getResources().getDrawable(R.drawable.block_circle, null));
            binding.inProgressCallRLView.setBackground(getDrawable(R.drawable.bg_red));
        }


        callerPhoneNumberTV.setText(PHONE_NUMBER);


        if (isMuted) {
            muteBtnName = "Unmute";
        } else {
            muteBtnName = "Mute";
        }

        if (isSpeakerOn) {
            speakerBtnName = "Speaker Off";
        } else {
            speakerBtnName = "Speaker On";
        }

        if (isSpeakerOn) {
            binding.speakerBtn.setBackgroundResource(R.drawable.rounded_padding_btn);
            binding.speakerBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.white)));
            NotificationHelper.createIngoingCallNotification(CallActivity.this, callManager.getCall(), "01:12:00", speakerBtnName, muteBtnName);
        }


        if (!isMuted) {
            binding.muteBtn.setEnabled(true);
            binding.muteBtn.setClickable(true);
            binding.muteBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.white)));
            binding.muteBtn.setBackgroundResource(R.drawable.rounded_padding_btn);
            binding.muteBtnTxt.setText(R.string.mute);
            NotificationHelper.createIngoingCallNotification(CallActivity.this, callManager.getCall(), "01:12:00", speakerBtnName, muteBtnName);
        } else {
            binding.muteBtn.setEnabled(true);
            binding.muteBtn.setClickable(true);
            binding.muteBtnTxt.setText(R.string.unmute);
            binding.muteBtn.setBackgroundResource(R.drawable.rounded_fill_btn);
            binding.muteBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
            NotificationHelper.createIngoingCallNotification(CallActivity.this, callManager.getCall(), "01:12:00", speakerBtnName, muteBtnName);
        }


        callingStatusTV.setText("Call in progress...");

    }

    private void callEnd() {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(NotificationHelper.NOTIFICATION_ID);
        unregisterReceiver(this.receiver);

        Log.d("CallActivity", "CallActivity callEnd");
        finishAndRemoveTask();
        String name = "";


        Log.d("callEnd CallActivity", "callEnd " + PHONE_NUMBER);


        if (CALLER_NAME == null) {
            name = getContactNameFromLocal(PHONE_NUMBER, CallActivity.this);
        } else {
            name = CALLER_NAME;
        }


        if (settingsPrefHelper.getShowCallerId()) {
            Intent contactDetailIntent = new Intent(CallActivity.this, CustomDialogActivity.class);
            contactDetailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ContactsDataDb dbContactsHelper = new ContactsDataDb(CallActivity.this);

            Contact contactdb = dbContactsHelper.getContactByPhoneNumber(PHONE_NUMBER);
            if (contactdb != null) {
                contactDetailIntent.putExtra("modal", contactdb);
                startActivity(contactDetailIntent);
            } else {
                if (Utils.isNetworkAvailable(CallActivity.this)) {
                    String finalName = name;
                    Utils.getContactDataDetails(PHONE_NUMBER, new Utils.ContactDataCallback() {
                        @Override
                        public void onSuccess(Object data) {
                            if (data != null) {
                                if (data instanceof UserProfile) {
                                    UserProfile profile = (UserProfile) data;
                                    String displayName = profile.getFirstName() + " " + profile.getLastName();
                                    dbContactsHelper.addContactOrUpdate(displayName, PHONE_NUMBER, true, false, "", "", null, null, "whocaller");
                                    contactDetailIntent.putExtra("modal", contactdb);
                                    startActivity(contactDetailIntent);

                                } else if (data instanceof Contact) {
                                    Contact contact = (Contact) data;
                                    contact.setIsWho(false);
                                    dbContactsHelper.addContactOrUpdate(contact);

                                    contactDetailIntent.putExtra("modal", contactdb);
                                    startActivity(contactDetailIntent);

                                } else {
                                    Log.e("ContactDataCallback", "Error: Unknown data type");
                                    contactDetailIntent.putExtra("callerName", finalName);
                                    contactDetailIntent.putExtra("callerNumber", PHONE_NUMBER);
                                    startActivity(contactDetailIntent);

                                }
                            } else {
                                Log.e("ContactDataCallback", "Error: Received null data");
                                contactDetailIntent.putExtra("callerName", finalName);
                                contactDetailIntent.putExtra("callerNumber", PHONE_NUMBER);
                                startActivity(contactDetailIntent);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e("ContactDataCallback", "Error: " + errorMessage);
                            contactDetailIntent.putExtra("callerName", finalName);
                            contactDetailIntent.putExtra("callerNumber", PHONE_NUMBER);
                            startActivity(contactDetailIntent);
                        }
                    });

                } else {
                    contactDetailIntent.putExtra("callerName", name);
                    contactDetailIntent.putExtra("callerNumber", PHONE_NUMBER);
                    startActivity(contactDetailIntent);
                }
            }
        }


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void calling() {


        inProgressCallRLView.setVisibility(View.GONE);
        incomingRLView.setVisibility(View.VISIBLE);


        PHONE_NUMBER = num;
        Log.d("CallActivity", "CallActivity calling: " + PHONE_NUMBER);

        if (blockCallerDbHelper.isPhoneNumberBlocked(PHONE_NUMBER)) {
            callManager.hangUpCall();
            callEnd();
        } else {
            shinyAnimator.start();
            inProgressCallRLView.setVisibility(View.GONE);
            incomingRLView.setVisibility(View.VISIBLE);


            PHONE_NUMBER = num;
            CALLER_NAME = getContactNameFromLocal(PHONE_NUMBER, CallActivity.this);
            Contact contact = contactsDataDb.getContactByPhoneNumber(PHONE_NUMBER);

            if (contact != null) {
                CALLER_NAME = contact.getName();
                binding.whoLay.setVisibility(View.VISIBLE);
            } else {
                if (Utils.isNetworkAvailable(this)) {
                    getContactData();
                }

            }
            incomingCallerNameTV.setText(CALLER_NAME);
            incomingCallerPhoneNumberTV.setText(PHONE_NUMBER);

            if (isContactStarred(PHONE_NUMBER, this)) {
                binding.favorite.setImageResource(R.drawable.favorite_fill);
                binding.favorite.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            }

            if (isValidName(CALLER_NAME)) {
                Bitmap contactImage = getContactImage(CallActivity.this, PHONE_NUMBER);
                if (contactImage != null) {
                    binding.profilePic.setImageBitmap(contactImage);
                } else {
                    binding.profilePic.setImageDrawable(getResources().getDrawable(R.drawable.ic_avatar, null));
                }
            } else {
                binding.profilePic.setImageDrawable(getResources().getDrawable(R.drawable.ic_avatar, null));
            }


            if (contact != null) {
                if (contact.getCountryName() != null) {
                    binding.countryName.setVisibility(View.VISIBLE);
                    binding.countryName.setText(contact.getCountryName());
                } else {
                    binding.countryName.setVisibility(View.VISIBLE);
                    binding.countryName.setText(Utils.getCountryNameFromPhoneNumber(this,PHONE_NUMBER));
                }
                if (contact.getCarrierName() != null) {
                    binding.carriorName.setVisibility(View.VISIBLE);
                    binding.carriorName.setText(contact.getCarrierName());
                }
                if (contact.isWho()) {
                    binding.whoProfile.setVisibility(View.VISIBLE);
                }

                if (contact.isSpam()) {
                    window.setStatusBarColor(getResources().getColor(R.color.red, null));
                    binding.profilePic.setImageDrawable(getResources().getDrawable(R.drawable.spam_circle, null));
                    binding.incomingRLView.setBackground(getDrawable(R.drawable.bg_red));
                }
                if (blockCallerDbHelper.isPhoneNumberBlocked(PHONE_NUMBER)) {
                    window.setStatusBarColor(getResources().getColor(R.color.red, null));
                    binding.profilePic2.setImageDrawable(getResources().getDrawable(R.drawable.block_circle, null));
                    binding.inProgressCallRLView.setBackground(getDrawable(R.drawable.bg_red));
                }
            }

            binding.countryName.setVisibility(View.VISIBLE);
            binding.countryName.setText(Utils.getCountryNameFromPhoneNumber(this,PHONE_NUMBER));

            NotificationHelper.createOutgoingNotification(this, callManager.getCall());

        }

    }

    private void setTime(String stringExtra) {
        callDurationTV.setVisibility(View.VISIBLE);
        callDurationTV.setText(stringExtra);
        if (!CallAnswerd){
            CallAnswerd = true;
            callingStatusTV.setText("Call Answerd");
            binding.holdBtn.setEnabled(true);
            binding.holdBtn.setClickable(true);
            binding.addCallBtn.setEnabled(true);
            binding.addCallBtn.setClickable(true);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint({"UseCompatTextViewDrawableApis", "SetTextI18n", "ClickableViewAccessibility", "UnspecifiedRegisterReceiverFlag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        Log.d("CallActivity", "CallActivity onCreate");

        Intent intent = getIntent();
        String getNumber = intent.getStringExtra("num");

        if (getNumber != null) {
            // PHONE_NUMBER = getNumber;
        }

        settingsPrefHelper = new SettingsPrefHelper(this);
        contactsDataDb = new ContactsDataDb(this);

        blockCallerDbHelper = new BlockCallerDbHelper(this);

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary, null));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CallManager.ACTION_CALL);
        intentFilter.addAction(CallManager.ACTION_TIME);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            registerReceiver(this.receiver, intentFilter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(this.receiver, intentFilter);
        }

        callManager = new CallManager(this);
        ringerManager = RingerManager.getInstance(this);

        initializeValues();
        addLockScreenFlags();
        wakeUpScreen();


        setButtonsDisabled();


        Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);


        fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnimation.setDuration(300);

        shinyAnimator = ObjectAnimator.ofFloat(arrowUp, "alpha", 0f, 1f);
        shinyAnimator.setDuration(800);
        shinyAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        shinyAnimator.setRepeatCount(ObjectAnimator.INFINITE);


        draggableButton.post(() -> {
            initialY = draggableButton.getY();
            topLimit = 0;
            bottomLimit = initialY + 320;
        });

        callerNameTV.setText(num);


        draggableButton.setOnTouchListener((view1, motionEvent) ->

        {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dY = view1.getY() - motionEvent.getRawY();
                    isButtonDragged = false;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float newY = motionEvent.getRawY() + dY;

                    newY = Math.max(topLimit, Math.min(bottomLimit, newY));

                    draggableButton.setY(newY);
                    arrowUp.setY(newY - arrowUp.getHeight() - 70);
                    arrowDown.setY(newY + draggableButton.getHeight() + 70);

                    isButtonDragged = true;

                    if (arrowUp.getY() < topLimit) {
                        arrowUp.startAnimation(AnimationUtils.loadAnimation(CallActivity.this, R.anim.fade_out));
                    } else {
                        arrowUp.clearAnimation();
                        arrowUp.setAlpha(1.0f);

                        if (!shinyAnimator.isRunning()) {
                            shinyAnimator.start();
                        }
                    }

                    if (arrowDown.getY() > bottomLimit) {
                        arrowDown.startAnimation(AnimationUtils.loadAnimation(CallActivity.this, R.anim.fade_out));
                    } else {
                        arrowDown.clearAnimation();
                        arrowDown.setAlpha(1.0f);
                    }
                    //int draggableButtonY = (int) draggableButton.getY();


                    return true;
                case MotionEvent.ACTION_UP:
                    if (isButtonDragged) {
                        if (draggableButton.getY() <= topLimit) {

                            CallManager.answerCall(callManager.getCall());
                            arrowDown.setVisibility(View.VISIBLE);
                            arrowUp.setVisibility(View.GONE);
                        } else if (draggableButton.getY() >= bottomLimit) {
                            CallManager.hangUpCall();
                            //callManager.hangup();
                            arrowUp.setVisibility(View.VISIBLE);
                            arrowDown.setVisibility(View.GONE);
                        } else {

                            draggableButton.animate().y(initialY).setDuration(300).start();
                            arrowUp.animate().y(initialY - arrowUp.getHeight() - 70).setDuration(300).start();
                            arrowDown.animate().y(initialY + draggableButton.getHeight() + 70).setDuration(300).start();


                            arrowUp.clearAnimation();
                            arrowUp.setAlpha(1.0f);
                            if (!shinyAnimator.isRunning()) {
                                shinyAnimator.start();
                            }

                            arrowDown.clearAnimation();
                            arrowDown.setAlpha(1.0f);
                        }
                    }


                    if (draggableButton.getY() > 0) {
                        actionBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black, null)));
                    }
                    return true;
                default:
                    return false;
            }
        });


        endCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callManager.hangUpCall();
               // finishAffinity();
            }
        });


        binding.holdBtn.setOnClickListener(v ->

        {
            if (isCallOnHold) {
                CallManager.unholdCall(callManager.getCall());
                binding.holdBtn.setBackgroundResource(R.drawable.rounded_padding_btn);
                binding.holdBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.white)));
                isCallOnHold = false;
            } else {
                CallManager.holdCall(callManager.getCall());
                binding.holdBtn.setBackgroundResource(R.drawable.rounded_fill_btn);
                binding.holdBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
                isCallOnHold = true;
            }
        });

        binding.muteBtn.setOnClickListener(v ->

        {
            if (isMuted) {
                CallManager.muteCall(false);
                binding.muteBtn.setBackgroundResource(R.drawable.rounded_padding_btn);
                binding.muteBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.white)));
                binding.muteBtnTxt.setText(R.string.mute);
                isMuted = false;

                NotificationHelper.createIngoingCallNotification(CallActivity.this, callManager.getCall(), "01:12:00", speakerBtnName, "Mute");
            } else {
                CallManager.muteCall(true);
                binding.muteBtn.setBackgroundResource(R.drawable.rounded_fill_btn);
                binding.muteBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
                binding.muteBtnTxt.setText(R.string.unmute);
                isMuted = true;

                NotificationHelper.createIngoingCallNotification(CallActivity.this, callManager.getCall(), "01:12:00", speakerBtnName, "Unmute");
            }
        });

        binding.speakerBtn.setOnClickListener(v ->

        {
            if (isSpeakerOn) {
                CallManager.speakerCall(false);
                binding.speakerBtn.setBackgroundResource(R.drawable.rounded_padding_btn);
                binding.speakerBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.white)));
                isSpeakerOn = false;

                NotificationHelper.createIngoingCallNotification(CallActivity.this, callManager.getCall(), "01:12:00", "Speaker On", muteBtnName);
            } else {
                CallManager.speakerCall(true);

                binding.speakerBtn.setBackgroundResource(R.drawable.rounded_fill_btn);
                binding.speakerBtn.setImageTintList(ColorStateList.valueOf(getColor(R.color.black)));
                isSpeakerOn = true;

                NotificationHelper.createIngoingCallNotification(CallActivity.this, callManager.getCall(), "01:12:00", "Speaker Off", muteBtnName);
            }
        });

        binding.keyPadBtn.setOnClickListener(v ->

        {

            keypadDialog = new BottomSheetDialog(CallActivity.this);
            keypadDialog.setContentView(R.layout.in_progress_call_dialpad);
            keypadDialog.setCanceledOnTouchOutside(true);
            keypadDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

            ImageButton keypadCancelBtn = keypadDialog.findViewById(R.id.keypadCancelBtn);
            assert keypadCancelBtn != null;
            keypadCancelBtn.setOnClickListener(v1 -> keypadDialog.cancel());

            FloatingActionButton endCallBottomSheet = keypadDialog.findViewById(R.id.endCallBtnBottomSheet);
            assert endCallBottomSheet != null;
            endCallBottomSheet.setOnClickListener(v1 -> CallManager.hangUpCall());

            initBottomSheetBtnsAndPlayDTMFtones(callManager.getCall(), keypadDialog, keypadDialog.findViewById(R.id.keypadDialogTextView));

            keypadDialog.show();
        });

        binding.addCallBtn.setOnClickListener(v ->

                startActivity(new Intent(this, MainActivity.class)));




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop ringer when activity is destroyed
        if (ringerManager != null) {
            ringerManager.stopRinging();
        }

        if (keypadDialog != null) {
            if (keypadDialog.isShowing()) {
                keypadDialog.dismiss();
            }
        }
    }

    private void initializeValues() {
        endCallBtn = findViewById(R.id.endCallBtn);

        muteBtn = findViewById(R.id.muteBtn);
        speakerBtn = findViewById(R.id.speakerBtn);
        holdBtn = findViewById(R.id.holdBtn);
        recordBtn = findViewById(R.id.recordBtn);
        addCallBtn = findViewById(R.id.addCallBtn);
        mergeCallBtn = findViewById(R.id.mergeCallBtn);

        draggableButton = findViewById(R.id.draggable_button);
        arrowUp = findViewById(R.id.arrow_up);
        arrowDown = findViewById(R.id.arrow_down);
        actionBtn = findViewById(R.id.actionBtn);

        callerNameTV = findViewById(R.id.callerName);
        callerPhoneNumberTV = findViewById(R.id.callerPhoneNumber);
        callDurationTV = findViewById(R.id.callingDuration);
        callingStatusTV = findViewById(R.id.callingStatus);

        inProgressCallRLView = findViewById(R.id.inProgressCallRLView);
        incomingRLView = findViewById(R.id.incomingRLView);

        incomingCallerPhoneNumberTV = findViewById(R.id.incomingCallerPhoneNumberTV);
        incomingCallerNameTV = findViewById(R.id.incomingCallerNameTV);
        ringingStatusTV = findViewById(R.id.ringingStatus);

    }

    @SuppressLint("SetTextI18n")
    private void initBottomSheetBtnsAndPlayDTMFtones(Call call, BottomSheetDialog keypadDialog, TextView keypadDialogTextView) {

        btn0 = keypadDialog.findViewById(R.id.btn0);
        btn01 = keypadDialog.findViewById(R.id.btn01);
        btn02 = keypadDialog.findViewById(R.id.btn02);
        btn03 = keypadDialog.findViewById(R.id.btn03);
        btn04 = keypadDialog.findViewById(R.id.btn04);
        btn05 = keypadDialog.findViewById(R.id.btn05);
        btn06 = keypadDialog.findViewById(R.id.btn06);
        btn07 = keypadDialog.findViewById(R.id.btn07);
        btn08 = keypadDialog.findViewById(R.id.btn08);
        btn09 = keypadDialog.findViewById(R.id.btn09);
        btnStar = keypadDialog.findViewById(R.id.btnStar);
        btnHash = keypadDialog.findViewById(R.id.btnHash);

        btn0.setOnClickListener(v -> {
            //CallManager.playDtmfTone(call, '0');
            keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "0");
        });

        btn0.setOnLongClickListener(v -> {
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "+");
            return true;
        });

        btn01.setOnClickListener(v -> {
            // CallManager.playDtmfTone(call, '1');
            keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "1");
        });
        btn02.setOnClickListener(v -> {
            //CallManager.playDtmfTone(call, '2');
            keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "2");
        });
        btn03.setOnClickListener(v -> {
            // CallManager.playDtmfTone(call, '3');
            keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "3");
        });
        btn04.setOnClickListener(v -> {
            // CallManager.playDtmfTone(call, '4');
            keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "4");
        });
        btn05.setOnClickListener(v -> {
            //CallManager.playDtmfTone(call, '5');
            keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "5");
        });
        btn06.setOnClickListener(v -> {
            //CallManager.playDtmfTone(call, '6');
            keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "6");
        });
        btn07.setOnClickListener(v -> {
            //CallManager.playDtmfTone(call, '7');
            keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "7");
        });
        btn08.setOnClickListener(v -> {
            //CallManager.playDtmfTone(call, '8');
            keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "8");
        });
        btn09.setOnClickListener(v -> {
            //CallManager.playDtmfTone(call, '9');
            keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "9");
        });
        btnStar.setOnClickListener(v -> {
            // CallManager.playDtmfTone(call, '*');
            keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "*");
        });
        btnHash.setOnClickListener(v -> {
            // CallManager.playDtmfTone(call, '#');
            keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "#");
        });
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    private void setButtonsDisabled() {
//        binding.muteBtn.setEnabled(false);
//        binding.muteBtn.setClickable(false);
//        binding.holdBtn.setEnabled(false);
//        binding.holdBtn.setClickable(false);
//        recordBtn.setEnabled(false);
//        recordBtn.setClickable(false);
//        binding.addCallBtn.setEnabled(false);
//        binding.addCallBtn.setClickable(false);

    }

    private void addLockScreenFlags() {
        // For Android 8.1+ (API 27+), use the new methods
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        // Add all necessary window flags for lock screen display
        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // For Android 9+ (API 28+), ensure we can turn screen on
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        }
    }

    private void wakeUpScreen() {
        try {
            // Get the power manager
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                // Check if screen is off
                if (!powerManager.isInteractive()) {
                    // Wake up the screen
                    PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                        "Whocaller:WakeUpScreen"
                    );
                    wakeLock.acquire(1000); // Hold for 1 second
                    wakeLock.release();
                }
            }
        } catch (Exception e) {
            Log.e("CallActivity", "Error waking up screen: " + e.getMessage());
        }
    }

    public void getContactData() {
        Utils.getContactDataDetails(PHONE_NUMBER, new Utils.ContactDataCallback() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    if (data instanceof UserProfile) {
                        UserProfile profile = (UserProfile) data;
                        CALLER_NAME = profile.getFirstName() + " " + profile.getLastName();
                    } else if (data instanceof Contact) {
                        Contact contactz = (Contact) data;
                        CALLER_NAME = contactz.getName();
                    } else {
                        Log.e("CallActivity", "getContactDataDetails: Unknown data type");
                    }
                } else {
                    Log.e("CallActivity", "getContactDataDetails: null");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("CallActivity", "getContactDataDetails: " + errorMessage);
                CALLER_NAME = getContactNameFromLocal(PHONE_NUMBER, CallActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Ensure screen stays on and is properly configured for lock screen
        addLockScreenFlags();
        wakeUpScreen();
        
        if (callManager == null || callManager.getCall() == null) {
            //finish();
        } else {
            callStatus(callManager.getCall().getState());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't clear flags in onPause for incoming calls - we want to stay visible
        // The activity should remain visible even when paused during incoming calls
    }
}
