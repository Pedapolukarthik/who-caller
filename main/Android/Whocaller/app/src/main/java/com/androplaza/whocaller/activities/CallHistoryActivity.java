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
import static com.androplaza.whocaller.utils.Utils.isValidName;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.androplaza.whocaller.adapter.CallLogAdapter;
import com.androplaza.whocaller.databinding.ActivityCallHistoryBinding;
import com.androplaza.whocaller.helpers.CallLogHelper;
import com.androplaza.whocaller.modal.CallLogItem;

import java.util.List;
import java.util.Objects;

public class CallHistoryActivity extends AppCompatActivity {


    ActivityCallHistoryBinding binding;
    String phoneNumber, contactName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        Intent intent = getIntent();
        if (intent != null) {
            phoneNumber = intent.getStringExtra("phoneNumber");
            contactName = intent.getStringExtra("name");

            binding.name.setText(contactName);
            binding.number.setText(phoneNumber);
        }


        if (isValidName(contactName)) {
            Bitmap contactImage = getContactImage(this, phoneNumber);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                binding.profilePic.setImageBitmap(Objects.requireNonNullElseGet(contactImage, () -> generateAvatar(contactName)));
            }
        } else {
            binding.profilePic.setImageBitmap(generateAvatar("U"));
        }


        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

        List<CallLogItem> callLogItems = CallLogHelper.getCallLogsForNumber(this, phoneNumber);

        CallLogAdapter callLogAdapter = new CallLogAdapter(callLogItems, this, false, false);
        callLogAdapter.setShowHeader(true);
        binding.recyclerview.setAdapter(callLogAdapter);


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        binding.backBtn.setOnClickListener(v -> callback.handleOnBackPressed());


    }


}