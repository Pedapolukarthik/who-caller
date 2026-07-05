package com.androplaza.whocaller.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.androplaza.whocaller.databinding.ActivityContactManageBinding;
import com.naliya.callerid.database.sqlite.ContactsDataDb;
import com.naliya.callerid.modal.Contact;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class ContactManageActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private Bitmap contactImage;
    ActivityContactManageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactManageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intents = getIntent();
        if (intents != null) {
         binding.firstNameEditText.setText(intents.getStringExtra("name"));
         binding.phoneNumberEditText.setText(intents.getStringExtra("phone"));
        }

        binding.uploadImageButton.setOnClickListener(v -> openImagePicker());
        binding.saveContactButton.setOnClickListener(v -> saveContact());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                contactImage = BitmapFactory.decodeStream(inputStream);
                binding.contactImageView.setImageBitmap(contactImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }

    private void saveContact() {
        String firstName = binding.firstNameEditText.getText().toString();
        String lastName = binding.lastNameEditText.getText().toString();
        String phoneNumber = binding.phoneNumberEditText.getText().toString();
        String email = binding.emailEditText.getText().toString();

        ContactsDataDb contactsDataDb = new ContactsDataDb(this);

        Contact contact = new Contact();
        contact.setName(firstName + " " + lastName);
        contact.setPhoneNumber(phoneNumber);

        contactsDataDb.addContactOrUpdate(contact);



        if (firstName.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "First Name and Phone Number are required", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        // Add new raw contact
        operations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // Insert the contact's name
        operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName)
                .build());

        // Insert the contact's phone number
        operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        // Insert the contact's email
        operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        // Insert the contact's photo if it exists
        if (contactImage != null) {
            byte[] imageBytes = getBytesFromBitmap(contactImage);
            operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, imageBytes)
                    .build());
        }

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
            Toast.makeText(this, "Contact created successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Log.e("ContactUtils", "Error creating contact", e);
            Toast.makeText(this, "Failed to create contact", Toast.LENGTH_SHORT).show();
        }
    }
}