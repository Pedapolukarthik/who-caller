/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.androplaza.whocaller.R;
import com.androplaza.whocaller.databinding.ActivityBlockSpamBinding;
import com.androplaza.whocaller.mainFragments.BlockSpamFragment;
import com.androplaza.whocaller.utils.Utils;

public class BlockSpamActivity extends AppCompatActivity {

    ActivityBlockSpamBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlockSpamBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        loadFragment(new BlockSpamFragment());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Utils.reDirectMainActivity(BlockSpamActivity.this);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        binding.backBtn.setOnClickListener(v -> callback.handleOnBackPressed());

        binding.bottomNavigationView.setItemIconTintList(null);
        binding.bottomNavigationView.setSelectedItemId(R.id.block_spam);

        binding.bottomNavigationView.setOnItemSelectedListener(i -> {
            Intent intent = new Intent(BlockSpamActivity.this, MainActivity.class);
            if (i.getItemId() == R.id.call_log) {
                intent.putExtra("FRAGMENT_TO_LOAD", "CALL_LOG");
            } else if (i.getItemId() == R.id.contacts) {
                intent.putExtra("FRAGMENT_TO_LOAD", "CONTACTS");
            } else if (i.getItemId() == R.id.setting) {
                intent = new Intent(BlockSpamActivity.this, SettingsActivity.class);
            } else if (i.getItemId() == R.id.block_spam) {
                intent.putExtra("FRAGMENT_TO_LOAD", "SETTINGS");
            }
            startActivity(intent);
            return true;
        });
    }

    public void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, fragment)
                    .commit();
        }
    }
}
