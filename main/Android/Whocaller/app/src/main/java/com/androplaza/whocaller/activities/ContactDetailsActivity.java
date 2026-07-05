/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.activities;

import static com.androplaza.whocaller.utils.Utils.getContactImage;
import static com.androplaza.whocaller.utils.Utils.getLookupKeyFromPhoneNumber;
import static com.androplaza.whocaller.utils.Utils.isContactStarred;
import static com.androplaza.whocaller.utils.Utils.isPhoneNumberSaved;
import static com.androplaza.whocaller.utils.Utils.isValidName;
import static com.androplaza.whocaller.utils.Utils.makeCall;
import static com.androplaza.whocaller.utils.Utils.showToast;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.naliya.callerid.database.prefs.AdsPrefHelper;
import com.naliya.callerid.database.prefs.ProfilePrefHelper;
import com.androplaza.whocaller.Config;
import com.androplaza.whocaller.R;
import com.androplaza.whocaller.adapter.CallLogAdapter;
import com.androplaza.whocaller.adapter.TagAdapter;
import com.androplaza.whocaller.ads.Ads;
import com.androplaza.whocaller.database.sqlite.BlockCallerDbHelper;
import com.androplaza.whocaller.database.sqlite.ContactsDataDb;
import com.androplaza.whocaller.databinding.ActivityContactDetailsBinding;
import com.androplaza.whocaller.helpers.CallLogHelper;
import com.androplaza.whocaller.modal.CallLogItem;
import com.androplaza.whocaller.modal.Contact;
import com.androplaza.whocaller.utils.PhoneNumberUtils;
import com.androplaza.whocaller.utils.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ContactDetailsActivity extends AppCompatActivity {


    String phoneNumber, phoneNumberNationalFormat, contactName, lookupKey, FormatPhoneNumber, contactBy, profileTypeName, spamType;
    private boolean isStarred;

    ActivityContactDetailsBinding binding;
    private BlockCallerDbHelper blockCallerDbHelper;
    boolean isBlock = false;
    boolean isSpam = false;
    boolean isWhoProfile = false;

    Window window;
    ContactsDataDb contactsDataDb;
    ProfilePrefHelper profilePrefHelper;
    String TAGS = null;
    Contact contactModal;

    AdsPrefHelper adsPrefHelper;
    private List<Contact> contactList = new ArrayList<>();


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Utils.reDirectMainActivity(ContactDetailsActivity.this);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        blockCallerDbHelper = new BlockCallerDbHelper(this);
        contactsDataDb = new ContactsDataDb(this);
        profilePrefHelper = new ProfilePrefHelper(ContactDetailsActivity.this);
        adsPrefHelper = new AdsPrefHelper(this);

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String regionCode = getRegionCode(this);

        if (Utils.isNetworkAvailable(this)) {
            Ads ads = new Ads(this);
            ads.loadingNativeAd(this);
        }
        String phone_number;
        Intent intent = getIntent();
        if (intent != null) {
            phone_number = intent.getStringExtra("phoneNumber");
            contactName = intent.getStringExtra("name");
            isBlock = intent.getBooleanExtra("isBlock", false);
            //isSpam = intent.getBooleanExtra("isSpam",false);


            if (intent.getStringExtra("whocaller") != null) {
                contactBy = intent.getStringExtra("whocaller");
                contactByText();
            }

            isWhoProfile = intent.getBooleanExtra("whoprofile", false);

            Log.d("ContactDetails spamcheck", "isspam1: " + isSpam);

            if (intent.getParcelableExtra("modal") != null) {
                contactModal = intent.getParcelableExtra("modal");
                phone_number = contactModal.getPhoneNumber();
                contactName = contactModal.getName();
                TAGS = contactModal.getTag();
                isSpam = contactModal.isSpam();
                spamType = contactModal.getSpamType();
                isWhoProfile = contactModal.isWho();

                if (contactModal.getContactsBy() != null) {
                    contactBy = contactModal.getContactsBy();
                    contactByText();
                }

                Log.d("ContactDetails spamcheck", "isspam2: " + isSpam);
            }


            phoneNumber = phone_number;

            if (contactName == null) {
                contactName = "Whocaller user";
            }

            String nationalFormat = PhoneNumberUtils.toNationalFormat(phone_number.replace("*", "").replace("#", ""), "US");
            if (nationalFormat != null) {
                binding.phoneNumber.setText(nationalFormat);
                phoneNumberNationalFormat = nationalFormat;
            } else {
                binding.phoneNumber.setText(phone_number.replace("*", "").replace("#", ""));
            }


            binding.contactName.setText(Utils.toTextCase(contactName));


            lookupKey = getLookupKeyFromPhoneNumber(phoneNumber, this);

            isStarred = isContactStarred(phoneNumber, this);
            simulateIconChange();


            Contact contact = contactsDataDb.getContactByPhoneNumber(phoneNumber);
            if (contact != null) {
                contactModal = contact;
                contactName = contact.getName();
                isSpam = contact.isSpam();
                TAGS = contact.getTag();
                spamType = contact.getSpamType();
                isWhoProfile = contact.isWho();
                if (contact.getContactsBy() != null) {
                    contactBy = contact.getContactsBy();
                }
                Log.d("ContactDetails spamcheck", "isspam3: " + isSpam);
            }


            Phonenumber.PhoneNumber phoneNumberr;
            FormatPhoneNumber = phoneNumber.replace("*", "").replace("#", "");
            try {
                phoneNumberr = phoneNumberUtil.parse(FormatPhoneNumber, regionCode);
            } catch (NumberParseException e) {
                throw new RuntimeException(e);
            }
            if (phoneNumberUtil.isValidNumber(phoneNumberr)) {
                FormatPhoneNumber = phoneNumberUtil.format(phoneNumberr, PhoneNumberUtil.PhoneNumberFormat.E164);
            }

            checkWhatsAppContact(FormatPhoneNumber);
            tagsAndNameChange();


            if (isWhoProfile) {
                binding.whoProfile.setImageDrawable(getDrawable(R.drawable.who));
            }


            if (contactBy != null) {
                if (contactBy.equals("whocaller")) {
                    binding.suggestLay.setVisibility(View.VISIBLE);
                    binding.editName.setVisibility(View.VISIBLE);
                }
            }


            window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            layoutChangers();
            contactByText();
            binding.profileTitleName.setText(profileTypeName);

            binding.favoriteBtn.setOnClickListener(v -> toggleStarStatus());

            binding.ivMenu.setOnClickListener(ContactDetailsActivity.this::showPopupMenu);

            binding.block.setOnClickListener(v -> showConfirmBlockDialog());

            binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

            List<CallLogItem> callLogItems = CallLogHelper.getCallLogsForNumber(this, phoneNumber);

            if (callLogItems.size() > 0) {
                CallLogAdapter callLogAdapter = new CallLogAdapter(callLogItems, this, true, false);
                callLogAdapter.setShowHeader(true);
                binding.recyclerview.setAdapter(callLogAdapter);
                binding.noCallsHistory.setVisibility(View.GONE);
                binding.viewAllLay.setVisibility(View.VISIBLE);

            } else {
                binding.viewAllLay.setVisibility(View.GONE);
                binding.noCallsHistory.setVisibility(View.VISIBLE);
            }


            if (contactModal != null && contactModal.getCarrierName() != null && contactModal.getCountryName() != null) {
                if (!contactModal.getCarrierName().equals("null")) {
                    binding.network.setText(contactModal.getCarrierName());
                    binding.location.setText(contactModal.getCountryName());
                }
            } else {

                binding.location.setText(Utils.getCountryNameFromPhoneNumber(this,phoneNumber));
                Utils.lookupCarrier(this,FormatPhoneNumber, new Utils.CarrierLookupCallback() {
                    @Override
                    public void onSuccess(String carrierName, String countryCode) {
                        runOnUiThread(() -> {
                            String countryName = new Locale("", countryCode).getDisplayCountry();
                            Contact contactx = new Contact();
                            if (carrierName != null && !carrierName.equals("null")) {
                                binding.network.setText(carrierName);
                                contactx.setCarrierName(carrierName);
                            } else {
                                binding.network.setText(getString(R.string.mobile));
                                contactx.setCarrierName("Mobile");
                            }
                            contactx.setCountryName(countryName);
                            binding.location.setText(countryName);

                            contactx.setName(contactName);
                            contactx.setPhoneNumber(phoneNumber);
                            contactx.setIsWho(isWhoProfile);
                            contactx.setIsSpam(isSpam);
                            contactx.setSpamType(spamType);
                            contactx.setTag(TAGS);
                            contactx.setCountryName(countryName);
                            contactx.setContactsBy(contactBy);


                            //contactsDataDb.addContactOrUpdate(contactx);
                            contactList.add(contactx);

                            if (isValidName(contactName) && !isWhoProfile) {
                                Utils.postContact(contactList, profilePrefHelper.getPhone());
                            }

                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() -> binding.network.setText(getString(R.string.mobile)));
                    }
                });


            }


            if (isPhoneNumberSaved(phoneNumber, this)) {
                binding.saveLay.setVisibility(View.GONE);
                binding.edit.setOnClickListener(v -> {

                    if (lookupKey == null) {
                        Utils.openContactEditPage(this, getLookupKeyFromPhoneNumber(phoneNumber, this));

                    } else {
                        Utils.openContactEditPage(this, lookupKey);
                    }

                });
            } else {
                binding.editLay.setVisibility(View.GONE);
                binding.save.setOnClickListener(v -> Utils.openContactCreatePage(this, phoneNumber, contactName));
            }

            binding.ivBack.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });


            binding.callBtn.setOnClickListener(v -> makeCall(phoneNumber, this));

            binding.callBtn2.setOnClickListener(v -> makeCall(phoneNumber, this));


            binding.viewAll.setOnClickListener(v -> {
                Intent intents = new Intent(this, CallHistoryActivity.class);
                intents.putExtra("name", contactName);
                intents.putExtra("phoneNumber", phoneNumber);
                startActivity(intents);
            });


            binding.editName.setOnClickListener(v -> showEditNameBottomSheet());

            binding.suggestName.setOnClickListener(v -> showEditNameBottomSheet());

            binding.addTagName.setOnClickListener(v -> showAddTagBottomSheet());

            binding.whatsappLay.setOnClickListener(v -> {
                Uri uri = Uri.parse("https://wa.me/" + FormatPhoneNumber);
                Intent intents = new Intent(Intent.ACTION_VIEW, uri);
                intents.setPackage("com.whatsapp");
                startActivity(intents);
            });
        }


    }

    private void contactByText() {
        if (contactBy != null) {
            if (contactBy.equals("whocaller")) {
                profileTypeName = "IDENTIFIED BY " + getResources().getString(R.string.app_name);
            }
        } else {
            profileTypeName = getResources().getString(R.string.in_your_contacts);
        }
    }

    private void checkWhatsAppContact(String contactNumber) {
        Uri uri = Uri.parse("https://wa.me/" + contactNumber);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.whatsapp");

        if (intent.resolveActivity(getPackageManager()) != null) {
            binding.whatsappLay.setVisibility(View.VISIBLE);
        }
    }

    private void tagsAndNameChange() {
        if (TAGS != null && !TAGS.equals("null") && !TAGS.isEmpty()) {
            binding.tagLay.setVisibility(View.VISIBLE);
            binding.tagName.setText(TAGS);
            binding.tagIcon.setImageDrawable(TagAdapter.getIcon(this, TAGS));
        } else {
            binding.tagLay.setVisibility(View.GONE);
        }

    }

    private void showAddTagBottomSheet() {
        List<String> tags = Arrays.asList("Education", "Entertainment & Arts", "Finance & Insurance", "Health & Wellness", "Hotels & Accommodation", "Nightlife & Drinks", "Restaurants & Cafés", "Services", "Shopping & Convenience Stores", "Transportation / Automotive", "Travel & Tourism", "Legal", "Beauty And Personal Care", "Property", "Religious Place", "Sports And Recreation");


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        @SuppressLint("InflateParams") View bottomSheetView = getLayoutInflater().inflate(R.layout.add_tag_bottom_layout, null);

        TagAdapter tagAdapter = new TagAdapter(ContactDetailsActivity.this, tags, tag -> {
            TAGS = tag;
            Toast.makeText(ContactDetailsActivity.this, "Selected: " + tag, Toast.LENGTH_SHORT).show();
        });

        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.tagRecyclerView);
        ImageView saveBtn = bottomSheetView.findViewById(R.id.save_btn);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(tagAdapter);

        saveBtn.setOnClickListener(v -> {
            if (TAGS != null) {
                Contact contact = new Contact();
                contact.setName(contactName);
                contact.setPhoneNumber(phoneNumber);
                contact.setIsWho(isWhoProfile);
                contact.setIsSpam(isSpam);
                contact.setSpamType(spamType);
                contact.setTag(TAGS);
                contact.setContactsBy(contactBy);

                contactsDataDb.addContactOrUpdate(contact);

                contactList.add(contact);

                if (isValidName(contactName) && !isWhoProfile) {
                    Utils.postContact(contactList, profilePrefHelper.getPhone());
                }


                bottomSheetDialog.cancel();
                tagsAndNameChange();
                showToast(ContactDetailsActivity.this, getString(R.string.toast_improving));
            } else {
                bottomSheetDialog.cancel();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void showEditNameBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ContactDetailsActivity.this);
        @SuppressLint("InflateParams") View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.edit_name_bottom_layout, null);

        ImageView closeButton = bottomSheetView.findViewById(R.id.close_btn);
        TextView saveBtn = bottomSheetView.findViewById(R.id.save);
        EditText editText = bottomSheetView.findViewById(R.id.edit_text);
        RadioGroup radioGroup = bottomSheetView.findViewById(R.id.radio_group);

        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        saveBtn.setOnClickListener(v -> {

            if (!editText.getText().toString().isEmpty()) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId != -1) {


                    if (selectedId == R.id.radio_business) {
                        spamType = "1";
                    } else if (selectedId == R.id.radio_person) {
                        spamType = "0";
                    }

                    Contact contact = new Contact();
                    contact.setName(editText.getText().toString());
                    contact.setPhoneNumber(phoneNumber);
                    contact.setIsWho(isWhoProfile);
                    contact.setIsSpam(isSpam);
                    contact.setSpamType(spamType);
                    contact.setTag(TAGS);
                    contact.setContactsBy(contactBy);

                    contactsDataDb.addContactOrUpdate(contact);

                    contactList.add(contact);
                    if (!isWhoProfile) {
                        Utils.postContact(contactList, profilePrefHelper.getPhone());
                    }
                    bottomSheetDialog.dismiss();
                    binding.contactName.setText(Utils.toTextCase(editText.getText().toString()));
                    tagsAndNameChange();
                    showToast(ContactDetailsActivity.this, getString(R.string.toast_improving));

                } else {
                    showToast(ContactDetailsActivity.this, "Please select an option");
                }

            } else {
                editText.setError("please enter valid name");
            }

        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();
    }


    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.contact_details_menu, popupMenu.getMenu());

        Menu menu = popupMenu.getMenu();

        if (isPhoneNumberSaved(phoneNumber, this)) {
            MenuItem saveItem = menu.findItem(R.id.save);
            saveItem.setVisible(false);
        } else {
            MenuItem editItem = menu.findItem(R.id.edit);
            editItem.setVisible(false);
        }


        if (isWhoProfile) {
            MenuItem report_contact = menu.findItem(R.id.report_contact);
            report_contact.setVisible(false);
            MenuItem revoke_report = menu.findItem(R.id.revoke_report);
            revoke_report.setVisible(false);
        } else {
            if (isSpam) {
                MenuItem report_contact = menu.findItem(R.id.report_contact);
                report_contact.setVisible(false);
                MenuItem revoke_report = menu.findItem(R.id.revoke_report);
                if (revoke_report != null) {
                    SpannableString s = new SpannableString(revoke_report.getTitle());
                    s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red, null)), 0, s.length(), 0);
                    revoke_report.setTitle(s);
                }
            } else {
                MenuItem revoke_report = menu.findItem(R.id.revoke_report);
                revoke_report.setVisible(false);
                MenuItem report_contact = menu.findItem(R.id.report_contact);
                if (report_contact != null) {
                    SpannableString s = new SpannableString(report_contact.getTitle());
                    s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red, null)), 0, s.length(), 0);
                    report_contact.setTitle(s);
                }
            }

        }


        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.share) {
                Utils.shareContact(ContactDetailsActivity.this, contactName, phoneNumber);
                return true;
            } else if (id == R.id.edit) {
                if (lookupKey == null) {
                    Utils.openContactEditPage(this, getLookupKeyFromPhoneNumber(phoneNumber, this));

                } else {
                    Utils.openContactEditPage(this, lookupKey);
                }
                return true;
            } else if (id == R.id.save) {
                Utils.openContactCreatePage(this, phoneNumber, contactName);
                return true;
            } else if (id == R.id.copy_name) {
                Utils.copyToClipboard(ContactDetailsActivity.this, contactName, "name");
                showToast(ContactDetailsActivity.this, "Copy Name succeeded");
                return true;
            } else if (id == R.id.copy_number) {
                Utils.copyToClipboard(ContactDetailsActivity.this, phoneNumber, "number");
                showToast(ContactDetailsActivity.this, "Copy Name succeeded");
                return true;
            } else if (id == R.id.copy_contact) {
                copyContactToClipboard(contactName, phoneNumber);
                showToast(ContactDetailsActivity.this, "Copy Contact succeeded");
                return true;
            } else if (id == R.id.report_contact) {
                new AlertDialog.Builder(ContactDetailsActivity.this).setTitle("Report Contact").setIcon(android.R.drawable.ic_dialog_alert).setMessage("Are you sure you want to report this contact? After reporting, this contact will be marked as spam. Please check and confirm.").setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    Contact contact = new Contact();
                    contact.setName(contactName);
                    contact.setPhoneNumber(phoneNumber);
                    contact.setIsWho(isWhoProfile);
                    contact.setIsSpam(isSpam = true);
                    contact.setSpamType(spamType);
                    contact.setTag(TAGS);
                    contact.setContactsBy(contactBy);
                    contactsDataDb.addContactOrUpdate(contact);
                    contactList.add(contact);
                    if (isValidName(contactName) && !isWhoProfile) {
                        Utils.postContact(contactList, profilePrefHelper.getPhone());
                    }
                    showToast(ContactDetailsActivity.this, getString(R.string.toast_improving));
                    layoutChangers();
                }).setNegativeButton(android.R.string.no, null).show();
                return true;
            } else if (id == R.id.revoke_report) {
                new AlertDialog.Builder(ContactDetailsActivity.this).setTitle("Revoke Report").setIcon(android.R.drawable.ic_dialog_alert).setMessage("Are you sure you want to revoke the report for this contact? After revoking, this contact will no longer be marked as spam. Please check and confirm.").setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    Contact contact = new Contact();
                    contact.setName(contactName);
                    contact.setPhoneNumber(phoneNumber);
                    contact.setIsWho(isWhoProfile);
                    contact.setIsSpam(isSpam = false);
                    contact.setSpamType(spamType);
                    contact.setTag(TAGS);
                    contact.setContactsBy(contactBy);
                    contactsDataDb.addContactOrUpdate(contact);
                    contactList.add(contact);
                    if (isValidName(contactName) && !isWhoProfile) {
                        Utils.postContact(contactList, profilePrefHelper.getPhone());
                    }
                    showToast(ContactDetailsActivity.this, "Report revoked successfully");
                    layoutChangers();
                }).setNegativeButton(android.R.string.no, null).show();
                return true;
            } else {
                return false;
            }
        });

        popupMenu.show();
    }


    private void showConfirmBlockDialog() {
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
        builder.setMessage(message).setTitle(title).setPositiveButton("Yes", (dialog, id) -> {
            if (isBlock) {
                if (blockCallerDbHelper.deleteBlockCallerByPhoneNumber(phoneNumber)) {
                    isBlock = false;
                    layoutChangers();
                    Log.d("Block number", "number: " + phoneNumber);
                }
            } else {
                if (blockCallerDbHelper.addBlockCaller(contactName, phoneNumber)) {
                    isBlock = true;
                    layoutChangers();
                    Log.d("Block number", "number: " + phoneNumber);
                }
            }
        }).setNegativeButton("No", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void layoutChangers() {

        if (isBlock || isSpam) {
            if (spamType != null && !spamType.equals("null")) {
                if (spamType.equals(Config.BUSINESS)) {
                    binding.typeBusiness.setVisibility(View.VISIBLE);
                } else if (spamType.equals(Config.PERSON)) {
                    binding.typePerson.setVisibility(View.VISIBLE);
                }
            }
            if (isBlock) {
                binding.contactImage.setImageDrawable(getResources().getDrawable(R.drawable.block_circle, null));
            } else if (isSpam) {
                binding.contactImage.setImageDrawable(getResources().getDrawable(R.drawable.spam_circle, null));
            }

        } else {
            binding.typeBusiness.setVisibility(View.GONE);
            binding.typePerson.setVisibility(View.GONE);

            if (isValidName(contactName)) {
                Bitmap contactImage = getContactImage(this, phoneNumber);
                if (contactImage != null) {
                    binding.contactImage.setImageBitmap(contactImage);
                } else {
                    binding.contactImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_avatar, null));
                }
            } else {
                binding.contactImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_avatar, null));
            }
        }

        if (isBlock || isSpam) {
            if (isBlock) {
                binding.blockText.setText(R.string.unblock);
            } else {
                binding.blockText.setText(R.string.block);
            }

            binding.bgLay.setBackgroundColor(getResources().getColor(R.color.red, null));
            window.setStatusBarColor(getResources().getColor(R.color.red, null));


        } else {
            binding.blockText.setText(R.string.block);
            binding.bgLay.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark, null));
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark, null));
        }

    }

    private void copyContactToClipboard(String name, String number) {
        String contactInfo = "Name: " + name + "\nNumber: " + number;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Contact Info", contactInfo);
        clipboard.setPrimaryClip(clip);
    }

    private void toggleStarStatus() {
        if (lookupKey != null && !lookupKey.isEmpty()) {
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            Uri contactUri = ContactsContract.Contacts.lookupContact(getContentResolver(), lookupUri);

            if (contactUri != null) {
                ContentValues values = new ContentValues();
                values.put(ContactsContract.Contacts.STARRED, isStarred ? 0 : 1);

                int rowsUpdated = getContentResolver().update(contactUri, values, null, null);

                if (rowsUpdated > 0) {
                    isStarred = !isStarred;
                    simulateIconChange();
                    Toast.makeText(this, contactName + (isStarred ? " is added to favourites" : " is removed from favourites"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to update contact", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please save contact, before added to favourites", Toast.LENGTH_SHORT).show();
        }
    }


    private String getRegionCode(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simCountryIso = telephonyManager.getSimCountryIso().toUpperCase();
        if (simCountryIso.isEmpty()) {
            simCountryIso = Locale.getDefault().getCountry();
        }
        return simCountryIso;
    }



    private void simulateIconChange() {
        if (isStarred) {
            binding.ivFav.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
        } else {
            binding.ivFav.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));


        }
    }


}





