/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.activities;



import static com.androplaza.whocaller.utils.Utils.generateAvatar;
import static com.androplaza.whocaller.utils.Utils.getContactImage;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.androplaza.whocaller.R;
import com.androplaza.whocaller.adapter.TagAdapter;
import com.androplaza.whocaller.ads.Ads;
import com.androplaza.whocaller.database.sqlite.BlockCallerDbHelper;
import com.androplaza.whocaller.databinding.ActivityCustomDialogBinding;
import com.androplaza.whocaller.utils.Utils;
import com.naliya.callerid.database.prefs.SettingsPrefHelper;
import com.naliya.callerid.modal.Contact;

public class CustomDialogActivity extends AppCompatActivity {

    Contact contactModal;
    Intent intent;

    ActivityCustomDialogBinding binding;
    boolean isBlock = false;

    BlockCallerDbHelper blockCallerDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        intent = getIntent();
        binding = ActivityCustomDialogBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        SettingsPrefHelper settingsPrefHelper = new SettingsPrefHelper(this);

        if (Utils.isNetworkAvailable(this)) {
            Ads ads = new Ads(this);
            ads.loadingNativeAd(this);
        }

        setFinishOnTouchOutside(false);

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();

            switch (settingsPrefHelper.getCallerIdPosition()) {
                case "Top":
                    params.gravity = Gravity.TOP;
                    break;
                case "Center":
                    params.gravity = Gravity.CENTER;
                    break;
                case "Bottom":
                    params.gravity = Gravity.BOTTOM;
                    break;
                default:
                    params.gravity = Gravity.CENTER;
                    break;
            }

            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.y = 0;
            window.setAttributes(params);
        }

        blockCallerDbHelper = new BlockCallerDbHelper(this);


        binding.closeIcon.setOnClickListener(v -> finish());


        if (intent.getParcelableExtra("modal") != null) {
            contactModal = intent.getParcelableExtra("modal");

            isBlock = blockCallerDbHelper.isPhoneNumberBlocked(contactModal.getPhoneNumber());
            dialogAction(contactModal.getPhoneNumber(), contactModal.getName());
            binding.callerName.setText(Utils.toTextCase(contactModal.getName()));
            binding.callerNumber.setText(contactModal.getPhoneNumber());

            if (contactModal.getPhoneNumber() != null){
                Bitmap contactImage = getContactImage(this, contactModal.getPhoneNumber());
                if (contactImage != null) {
                    binding.profilePic.setImageBitmap(contactImage);
                } else {
                    binding.profilePic.setImageBitmap(generateAvatar(Utils.isValidName(contactModal.getName()) ? contactModal.getName() : "U"));
                }
            }

            String CarrierName = contactModal.getCarrierName();
            String CountryName = contactModal.getCountryName();

            if (contactModal.getCarrierName() != null && !CarrierName.equals("null")) {
                binding.network.setText(contactModal.getCarrierName());
            }else {
                getNumberData(contactModal.getPhoneNumber());
            }

            if (contactModal.getCountryName() != null && !CountryName.equals("null")) {
                binding.country.setText(contactModal.getCountryName());
            }else {
                getNumberData(contactModal.getPhoneNumber());
            }

            if (contactModal.isWho()) {
                binding.whoProfile.setVisibility(View.VISIBLE);
            }
            //binding.profilePic.setImageBitmap(Utils.generateAvatar(Utils.isValidName(contactModal.getName()) ? contactModal.getName() : "U"));

            if (contactModal.isSpam()) {
                binding.first.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red, null)));
                binding.viewProfile.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_low, null)));
                binding.profilePic.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.spam_circle));
            }

            if (isBlock) {
                binding.blockTxt.setText(getString(R.string.unblock));
                binding.profilePic.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.block_circle));
                binding.first.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red, null)));
                binding.viewProfile.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_low, null)));
            } else {
                binding.blockTxt.setText(getString(R.string.block));
            }

            if (contactModal.getTag() != null && !contactModal.getTag().equals("null") && !contactModal.getTag().isEmpty()) {
                binding.tagLay.setVisibility(View.VISIBLE);
                binding.tagName.setText(contactModal.getTag());
                binding.tagIcon.setImageDrawable(TagAdapter.getIcon(this, contactModal.getTag()));
            }

            binding.btnBlock.setOnClickListener(v -> showConfirmBlockDialog(contactModal.getPhoneNumber(), contactModal.getName()));

            binding.viewProfile.setOnClickListener(v -> {
                Intent contactDetailIntent = new Intent(CustomDialogActivity.this, ContactDetailsActivity.class);
                contactDetailIntent.putExtra("modal", contactModal);
                contactDetailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(contactDetailIntent);
                finish();
            });

        }


        if (intent.getStringExtra("callerName") != null && intent.getStringExtra("callerNumber") != null) {
            String callerName = intent.getStringExtra("callerName");
            String callerNumber = intent.getStringExtra("callerNumber");
            getNumberData(callerNumber);
            isBlock = blockCallerDbHelper.isPhoneNumberBlocked(callerNumber);
            dialogAction(callerNumber, callerName);
            binding.callerName.setText(Utils.toTextCase(callerName));
            binding.callerNumber.setText(callerNumber);

            if (isBlock) {
                binding.blockTxt.setText(getString(R.string.unblock));
                binding.profilePic.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.block_circle));
            } else {
                binding.blockTxt.setText(getString(R.string.block));
            }

            if (callerNumber != null){
                Bitmap contactImage = getContactImage(this, callerNumber);
                if (contactImage != null) {
                    binding.profilePic.setImageBitmap(contactImage);
                } else {
                    binding.profilePic.setImageBitmap(generateAvatar(Utils.isValidName(callerName) ? callerNumber : "U"));
                }
            }



            binding.btnBlock.setOnClickListener(v -> showConfirmBlockDialog(callerNumber, callerName));

            binding.viewProfile.setOnClickListener(v -> {
                Intent contactDetailIntent = new Intent(CustomDialogActivity.this, ContactDetailsActivity.class);
                contactDetailIntent.putExtra("name", callerName);
                contactDetailIntent.putExtra("phoneNumber", callerNumber);
                contactDetailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(contactDetailIntent);
                finish();
            });

        }


    }

    public void dialogAction(String number, String name) {


        if (Utils.isPhoneNumberSaved(number, this)) {
            binding.btnSave.setVisibility(View.GONE);
            binding.btnEdit.setVisibility(View.VISIBLE);
            binding.btnEdit.setOnClickListener(v -> {
                String lKey = Utils.getLookupKeyFromPhoneNumber(number, CustomDialogActivity.this);
                if (lKey != null) {
                    Utils.openContactEditPage(CustomDialogActivity.this, lKey);
                }
            });

        } else {
            binding.btnEdit.setVisibility(View.GONE);
            binding.btnSave.setVisibility(View.VISIBLE);
            binding.btnSave.setOnClickListener(v -> Utils.openContactCreatePage(CustomDialogActivity.this, number, name));

        }


        binding.btnCall.setOnClickListener(v -> Utils.makeCall(number, CustomDialogActivity.this));
    }


    private void showConfirmBlockDialog(String number, String name) {

        String message;
        String title;

        if (isBlock) {
            message = "Do you want to Unblock this contact?";
            title = "Confirm Unblock";
        } else {
            message = "Do you want to block this contact?";
            title = "Confirm block";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton("Yes", (dialog, id) -> {
                    if (isBlock) {
                        if (blockCallerDbHelper.deleteBlockCallerByPhoneNumber(number)) {
                            isBlock = false;

                        }
                    } else {
                        if (blockCallerDbHelper.addBlockCaller(name, number)) {
                            isBlock = true;

                        }
                    }
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getNumberData(String phoneNumber){
        binding.country.setText(Utils.getCountryNameFromPhoneNumber(this,phoneNumber));

        Utils.lookupCarrier(this,Utils.getFormatNumber(this,phoneNumber), new Utils.CarrierLookupCallback() {
            @Override
            public void onSuccess(String carrierName, String countryCode) {
                runOnUiThread(() -> {
                    if (carrierName != null && !carrierName.equals("null")) {
                        binding.network.setText(carrierName);
                    } else {
                        binding.network.setText(getString(R.string.mobile));
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> binding.network.setText(getString(R.string.mobile)));
            }
        });

    }


}
