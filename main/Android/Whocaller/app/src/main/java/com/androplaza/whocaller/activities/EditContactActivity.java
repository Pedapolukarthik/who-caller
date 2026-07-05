package com.androplaza.whocaller.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.androplaza.whocaller.R;
import com.androplaza.whocaller.databinding.ActivityEditContactBinding;
import com.naliya.callerid.database.sqlite.ContactsDataDb;
import com.naliya.callerid.modal.Contact;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class EditContactActivity extends AppCompatActivity {

    private ActivityEditContactBinding binding;
    private String lookupKey;
    private static final int REQUEST_IMAGE_PICK = 100;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        lookupKey = getIntent().getStringExtra("lookupKey");
        if (lookupKey != null) {
            loadContactDetails(lookupKey);
        } else {
            showToast("No contact found.");
            finish();
        }

        binding.btnSaveContact.setOnClickListener(view -> {
            try {
                saveContact();
            } catch (RemoteException | OperationApplicationException e) {
                Log.e("ContactEdit", "Error saving contact", e);
                showToast("Failed to save contact");
            }
        });

        binding.btnUploadImage.setOnClickListener(v -> selectImageFromGallery());
        binding.backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void loadContactDetails(String lookupKey) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
        Uri contactUri = ContactsContract.Contacts.lookupContact(getContentResolver(), lookupUri);

        if (contactUri != null) {
            String[] projection = {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_URI
            };

            try (Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));

                    binding.etName.setText(displayName != null ? displayName : "N/A");
                    if (photoUri != null) {
                        binding.contactImage.setImageURI(Uri.parse(photoUri));
                    } else {
                        binding.contactImage.setImageResource(R.drawable.profile_img); // Placeholder image
                    }

                    fetchPhoneNumber(contactId);
                    fetchEmail(contactId);
                } else {
                    Log.e("ContactEdit", "No contact found.");
                    showToast("No contact details found");
                }
            } catch (Exception e) {
                Log.e("ContactEdit", "Error fetching contact details", e);
                showToast("Error fetching contact details");
            }
        } else {
            showToast("Contact not found");
            Log.e("ContactEdit", "Contact URI is null");
        }
    }

    private void fetchPhoneNumber(String contactId) {
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER };

        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{contactId};

        try (Cursor cursor = getContentResolver().query(phoneUri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                binding.etPhone.setText(phoneNumber != null ? phoneNumber : "N/A");
            } else {
                binding.etPhone.setText("N/A");
            }
        } catch (Exception e) {
            Log.e("ContactEdit", "Error fetching phone number", e);
        }
    }

    private void fetchEmail(String contactId) {
        Uri emailUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] projection = { ContactsContract.CommonDataKinds.Email.ADDRESS };

        String selection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{contactId};

        try (Cursor cursor = getContentResolver().query(emailUri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                String email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                binding.etEmail.setText(email != null ? email : "N/A");
            } else {
                binding.etEmail.setText("N/A");
            }
        } catch (Exception e) {
            Log.e("ContactEdit", "Error fetching email", e);
        }
    }

    private void saveContact() throws RemoteException, OperationApplicationException {
        String name = binding.etName.getText().toString();
        String phone = binding.etPhone.getText().toString();
        String email = binding.etEmail.getText().toString();


        ContactsDataDb contactsDataDb = new ContactsDataDb(this);

        Contact contact = new Contact();
        contact.setName(name);
        contact.setPhoneNumber(phone);
        contactsDataDb.addContactOrUpdate(contact);

        // Fetch the raw_contact_id using the lookup key
        String rawContactId = getRawContactId(lookupKey);

        if (rawContactId == null) {
            showToast("Failed to update contact: raw_contact_id not found");
            return;
        }

        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        if (!name.isEmpty()) {
            operations.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND " +
                            ContactsContract.Data.MIMETYPE + "=?", new String[]{rawContactId,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build());
        }

        if (!phone.isEmpty()) {
            operations.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND " +
                            ContactsContract.Data.MIMETYPE + "=?", new String[]{rawContactId,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                    .build());
        }

        if (!email.isEmpty()) {
            operations.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND " +
                            ContactsContract.Data.MIMETYPE + "=?", new String[]{rawContactId,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                    .build());
        }

        if (imageUri != null) {
            saveContactPhoto(rawContactId, imageUri, operations);
        }

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
            showToast("Contact updated successfully");
            finish();
        } catch (Exception e) {
            Log.e("ContactEdit", "Failed to update contact", e);
            showToast("Failed to update contact");
        }
    }

    private String getRawContactId(String lookupKey) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
        Uri contactUri = ContactsContract.Contacts.lookupContact(getContentResolver(), lookupUri);

        String rawContactId = null;

        if (contactUri != null) {
            String[] projection = new String[]{ContactsContract.RawContacts._ID};
            Cursor cursor = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                    projection,
                    ContactsContract.RawContacts.CONTACT_ID + "=?",
                    new String[]{getContactIdFromLookupKey(lookupKey)},
                    null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    rawContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
                }
                cursor.close();
            }
        }
        return rawContactId;
    }

    private String getContactIdFromLookupKey(String lookupKey) {
        String contactId = null;
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
        Uri contactUri = ContactsContract.Contacts.lookupContact(getContentResolver(), lookupUri);

        if (contactUri != null) {
            String[] projection = new String[]{ContactsContract.Contacts._ID};
            Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                cursor.close();
            }
        }
        return contactId;
    }
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            binding.contactImage.setImageURI(imageUri);
        }
    }

    private void saveContactPhoto(String rawContactId, Uri imageUri, ArrayList<ContentProviderOperation> operations) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageBytes = stream.toByteArray();

            String[] projection = { ContactsContract.Data._ID };
            String selection = ContactsContract.Data.RAW_CONTACT_ID + "=? AND " +
                    ContactsContract.Data.MIMETYPE + "=?";
            String[] selectionArgs = new String[]{rawContactId, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE};

            Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                operations.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(selection, selectionArgs)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, imageBytes)
                        .build());
            } else {
                operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, imageBytes)
                        .build());
            }

            if (cursor != null) {
                cursor.close();
            }

        } catch (FileNotFoundException e) {
            Log.e("ContactEdit", "File not found: " + imageUri, e);
        } catch (IOException e) {
            Log.e("ContactEdit", "Error processing image: " + imageUri, e);
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
