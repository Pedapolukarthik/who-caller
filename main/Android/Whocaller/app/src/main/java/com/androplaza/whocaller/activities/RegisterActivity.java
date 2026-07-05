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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.androplaza.whocaller.R;
import com.androplaza.whocaller.api.ApiClient;
import com.androplaza.whocaller.databinding.ActivityRegisterBinding;
import com.androplaza.whocaller.modal.UserProfile;
import com.androplaza.whocaller.utils.Utils;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String countryCode;

    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();


        countryCode = binding.countryPicker.getDefaultCountryCode();
        binding.countryPicker.setOnCountryChangeListener(() -> countryCode = binding.countryPicker.getSelectedCountryCode());

        binding.btnLogin.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        binding.btnSign.setOnClickListener(v -> {
            if (Utils.isNetworkAvailable(RegisterActivity.this)) {
                if (validation()) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.registerText.setVisibility(View.GONE);
                    binding.btnLogin.setEnabled(false);
                    registerWithEmail();
                }
            } else {
                Utils.showToast(RegisterActivity.this, getResources().getString(R.string.check_internet));
            }

        });

        binding.btnRegisterPhone.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
            startActivity(intent);
        });

        binding.backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    public boolean validation() {
        if (Objects.requireNonNull(binding.edUsername.getText()).toString().isEmpty()) {
            binding.edUsername.setError(getString(R.string.ename));
            return false;
        }
        if (Objects.requireNonNull(binding.edEmail.getText()).toString().isEmpty()) {
            binding.edEmail.setError(getString(R.string.evalisemail));
            return false;
        }
        if (binding.edAlternatmob.getText().toString().isEmpty()) {
            binding.edAlternatmob.setError(getString(R.string.evalidmobile));
            return false;
        }
        if (Objects.requireNonNull(binding.edPassword.getText()).toString().isEmpty()) {
            binding.edPassword.setError(getString(R.string.epassword));
            return false;
        }
        return true;
    }


    private void registerWithEmail() {
        String firstName = binding.edUsername.getText().toString().trim();
        String email = binding.edEmail.getText().toString().trim();
        String password = binding.edPassword.getText().toString().trim();
        String number = binding.edAlternatmob.getText().toString().trim();

        String phone = "+" + countryCode + number;


        UserProfile userProfile = new UserProfile(firstName, "", email, phone, password, "");
        ApiClient.ApiService apiService = ApiClient.getClient().create(ApiClient.ApiService.class);
        Call<Void> call = apiService.createUserProfileOnEmail(userProfile);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    saveUserToFirebase(email, password, userProfile);
                } else {
                    Toast.makeText(RegisterActivity.this, "Failed to create user profile", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }


    private void saveUserToFirebase(String email, String password, UserProfile userProfile) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Utils.getProfileData(RegisterActivity.this, userProfile);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }


}

