/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.activities;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.naliya.callerid.database.prefs.ProfilePrefHelper;
import com.androplaza.whocaller.Config;
import com.androplaza.whocaller.R;
import com.androplaza.whocaller.ads.Ads;
import com.androplaza.whocaller.database.sqlite.BlockCallerDbHelper;
import com.androplaza.whocaller.database.sqlite.ContactsDataDb;
import com.androplaza.whocaller.databinding.ActivityProfileBinding;
import com.androplaza.whocaller.utils.Utils;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    private FirebaseAuth mAuth;

    String FirstName, LastName, Email, Phone, ImageUrl;

    String TAG = "ProfileActivity";
    int PROFILE_SCORE = 0;

    @SuppressLint({"Range", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        ProfilePrefHelper profilePrefHelper = new ProfilePrefHelper(this);
        ContactsDataDb contactsDataDb = new ContactsDataDb(this);
        BlockCallerDbHelper blockCallerDbHelper = new BlockCallerDbHelper(this);


        FirstName = profilePrefHelper.getFirstName();
        LastName = profilePrefHelper.getLastName();
        Phone = profilePrefHelper.getPhone();
        Email = profilePrefHelper.getEmail();
        ImageUrl = profilePrefHelper.getImage();

        if (Utils.isNetworkAvailable(this)) {
            Ads ads = new Ads(this);
            ads.showBannerAd(this);
        }


        if (Phone == null || Phone.isEmpty()) {
            binding.profileNumber.setVisibility(View.GONE);
        } else {
            binding.profileNumber.setText(Phone);
        }

        PROFILE_SCORE = ProfilePrefHelper.getProfileScore(this);

        if (PROFILE_SCORE < 90) {
            binding.profileProgress.setVisibility(View.VISIBLE);
            binding.profileScore.setVisibility(View.VISIBLE);
            binding.editProfileName.setText(R.string.complete_your_profile);
            binding.nextProfile.setText(ProfilePrefHelper.nextProfileStatus(this));
            binding.profileScore.setText(PROFILE_SCORE + "%");
            binding.profileProgress.setProgress(PROFILE_SCORE);
        } else {
            binding.scoreLay.setVisibility(View.GONE);
            binding.editProfileName.setText(R.string.edit_profile);
        }


        String fullName;

        if (LastName == null || LastName.isEmpty()) {
            fullName = FirstName;
        } else {
            fullName = FirstName + " " + LastName;
        }
        binding.profileName.setText(Utils.toTextCase(fullName));


        if (Utils.isValidURL(ImageUrl)) {
            Glide.with(getApplicationContext())
                    .load(ImageUrl)
                    .placeholder(R.drawable.profile_img)
                    .error(R.drawable.profile_img)
                    .into(binding.profilePic);
        } else {
            Glide.with(getApplicationContext())
                    .load(Config.BASE_URL + "/public/storage/" + ImageUrl)
                    .placeholder(R.drawable.profile_img)
                    .error(R.drawable.profile_img)
                    .into(binding.profilePic);
        }


        binding.logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            profilePrefHelper.saveUserProfile("", "", "", "", "");
            profilePrefHelper.setIsSignUser(false);
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        binding.editProfileName.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, MyAccountActivity.class)));

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);



        binding.backBtn.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        });


        binding.tvIncomingCallsCount.setText(String.valueOf(profilePrefHelper.getIncomingCallsCount()));

        binding.tvOutgoingCallsCount.setText(String.valueOf(profilePrefHelper.getOutgoingCallsCount()));

        binding.tvUnknownNumber.setText(String.valueOf(contactsDataDb.getAllContacts().size()));

        binding.tvSpamCallsCount.setText(String.valueOf(contactsDataDb.getSpamContacts().size()));

        binding.tvBlockSavedCount.setText(String.valueOf(blockCallerDbHelper.getAllBlockCallers().size()));

        binding.setting.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SettingsActivity.class)));


    }





}