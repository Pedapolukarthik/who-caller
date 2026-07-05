/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.androplaza.whocaller.R;
import com.androplaza.whocaller.BuildConfig;

import com.androplaza.whocaller.ads.Ads;
import com.androplaza.whocaller.databinding.ActivityAboutBinding;
import com.androplaza.whocaller.utils.Utils;
import com.naliya.callerid.database.prefs.SettingsPrefHelper;


public class AboutActivity extends AppCompatActivity {
    ActivityAboutBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        SettingsPrefHelper settingsPrefHelper = new SettingsPrefHelper(this);

        binding.version.setText(getString(R.string.version) + " " + BuildConfig.VERSION_CODE + " (" + BuildConfig.VERSION_NAME + ")");
        binding.company.setText(settingsPrefHelper.getAppDevelopedBy());
        binding.email.setText(settingsPrefHelper.getAppEmail());
        binding.website.setText(settingsPrefHelper.getAppWebSite());
        binding.contact.setText(settingsPrefHelper.getAppContact());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        binding.backBtn.setOnClickListener(v -> callback.handleOnBackPressed());

        if (Utils.isNetworkAvailable(this)) {
            Ads ads = new Ads(this);
            ads.loadingNativeAdSmall(this);
        }

    }
}

