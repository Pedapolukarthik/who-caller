/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.activities;

import static com.androplaza.whocaller.tabsFragments.RecentsTabFragment.adapter;
import static com.androplaza.whocaller.utils.Utils.makeCall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naliya.callerid.AppCompatActivity;
import com.naliya.callerid.database.prefs.ProfilePrefHelper;
import com.androplaza.whocaller.Config;
import com.androplaza.whocaller.R;
import com.androplaza.whocaller.adapter.CallLogAdapter;
import com.androplaza.whocaller.adapter.ContactAdapter;
import com.androplaza.whocaller.adapter.OnlineContactAdapter;
import com.androplaza.whocaller.ads.Ads;
import com.androplaza.whocaller.database.sqlite.ContactsDataDb;
import com.androplaza.whocaller.databinding.ActivityMainBinding;
import com.androplaza.whocaller.mainFragments.CallLogFragment;
import com.androplaza.whocaller.mainFragments.ContactFragment;
import com.androplaza.whocaller.modal.Contact;
import com.androplaza.whocaller.utils.Permission;
import com.androplaza.whocaller.utils.Utils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements CallLogAdapter.SelectionListener {

    Button btn0, btn01, btn02, btn03, btn04, btn05, btn06, btn07, btn08, btn09, btnStar, btnHash;
    BottomSheetDialog keypadDialog;

    ActivityMainBinding binding;

    private List<Contact> filteredContactList = new ArrayList<>();
    private List<Contact> onlineContactList = new ArrayList<>();
    private List<Contact> databaseFilterContactList = new ArrayList<>();
    private ContactAdapter contactAdapter;

    private ContactsDataDb databaseHelper;
    private List<Contact> databaseContactList = new ArrayList<>();
    private OnlineContactAdapter databaseContactAdapter;
    private List<Contact> contactList = new ArrayList<>();

    String FirstName, LastName, Email, Phone, ImageUrl;


    Ads ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ads = new Ads(this);

        if (Ads.InterAddLoad) {
            ads.showInterstitialAd(this);
        }
        Intent intent = getIntent();

        if (intent != null) {
            String fragmentToLoad = intent.getStringExtra("FRAGMENT_TO_LOAD");
            if (fragmentToLoad != null) {
                switch (fragmentToLoad) {
                    case "CALL_LOG":
                        binding.bottomNavigationView.setSelectedItemId(R.id.call_log);
                        loadFragment(new CallLogFragment());
                        break;
                    case "CONTACTS":
                        binding.bottomNavigationView.setSelectedItemId(R.id.contacts);
                        loadFragment(new ContactFragment());
                        break;
                    case "SETTINGS":
                        startActivity(new Intent(this, SettingsActivity.class));
                        break;
                }
            } else {
                binding.bottomNavigationView.setSelectedItemId(R.id.call_log);
                loadFragment(new CallLogFragment());
            }
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPressed();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        ProfilePrefHelper profilePrefHelper = new ProfilePrefHelper(this);
        FirstName = profilePrefHelper.getFirstName();
        LastName = profilePrefHelper.getLastName();
        Phone = profilePrefHelper.getPhone();
        Email = profilePrefHelper.getEmail();
        ImageUrl = profilePrefHelper.getImage();

        if (!Utils.isValidPhoneNumber(Phone)) {
            new Handler().postDelayed(() -> {
                if (!isFinishing()) {
                    PhoneNumberDialog();
                }
            }, 500);

        }

        if (Utils.isValidURL(ImageUrl)) {
            Glide.with(this)
                    .load(ImageUrl)
                    .placeholder(R.drawable.profile_img)
                    .error(R.drawable.profile_img)
                    .into(binding.profile);
        } else {
            Glide.with(this)
                    .load(Config.BASE_URL + "/public/storage/" + ImageUrl)
                    .placeholder(R.drawable.profile_img)
                    .error(R.drawable.profile_img)
                    .into(binding.profile);
        }


        binding.bottomNavigationView.setItemIconTintList(null);

        binding.bottomNavigationView.setOnItemSelectedListener(i -> {

            if (i.getItemId() == R.id.call_log) {
                loadFragment(new CallLogFragment());
            } else if (i.getItemId() == R.id.contacts) {
                loadFragment(new ContactFragment());
            } else if (i.getItemId() == R.id.setting) {
                if (Ads.InterAddLoad) {
                    ads.showInterstitialAd(this);

                }
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (i.getItemId() == R.id.block_spam) {
                if (Ads.InterAddLoad) {
                    ads.showInterstitialAd(this);

                }
                startActivity(new Intent(this, BlockSpamActivity.class));
            }
            return true;
        });


        if (!Permission.checkPermissions(this)) {
            Permission.requestPermissions(this);
        }


        binding.searchBtn.setOnClickListener(v -> {
            binding.searchView.setIconified(false);
            binding.appbar.setVisibility(View.GONE);
            binding.flFragment.setVisibility(View.GONE);
            binding.bottomNavigationView.setVisibility(View.GONE);
            binding.searchLay.setVisibility(View.VISIBLE);
            binding.dialPad.setVisibility(View.GONE);

            binding.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            binding.databaseRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            contactAdapter = new ContactAdapter(filteredContactList, MainActivity.this, false, false);
            binding.recyclerView.setAdapter(contactAdapter);


            databaseContactAdapter = new OnlineContactAdapter(databaseFilterContactList, MainActivity.this);
            binding.databaseRecycler.setAdapter(databaseContactAdapter);
            databaseHelper = new ContactsDataDb(MainActivity.this);
            databaseContactList = databaseHelper.getAllContacts();


            if (!Permission.checkPermissions(this)) {
                Permission.requestPermissions(this);
            } else {
                new Utils.ContactLoaderTask(MainActivity.this, null, contacts -> {
                    Log.d("SavedTabFragment", "onContactsLoaded");
                    contactList = contacts;
                }).execute();
            }


        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });

        binding.profile.setOnClickListener(v -> {
            if (Ads.InterAddLoad) {
                ads.showInterstitialAd(this);

            }
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        binding.backBtn.setOnClickListener(v -> backPressed());

        binding.searchItem.setOnClickListener(v -> {

            String txt = binding.searchView.getQuery().toString();

            Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();

            if (Utils.isNetworkAvailable(MainActivity.this)) {
                if (txt.length() >= 4) {
                    Intent intents = new Intent(MainActivity.this, SearchActivity.class);
                    intents.putExtra("SEARCH", txt);
                    startActivity(intents);
                } else {
                    Utils.showToast(this, "You need to enter the last 4 characters");
                }

            } else {
                Utils.showToast(MainActivity.this, getResources().getString(R.string.check_internet));
            }


        });

        binding.selectAllButton.setOnClickListener(v -> {
            if (adapter != null) {
                if (adapter.isAllSelected()) {
                    adapter.deselectAll();
                } else {
                    adapter.selectAll();
                }
            }
        });

        binding.deleteButton.setOnClickListener(v -> adapter.deleteSelectedItems());

        binding.dialPad.setOnClickListener(v -> {
            keypadDialog = new BottomSheetDialog(MainActivity.this);
            keypadDialog.setContentView(R.layout.call_dialpad);
            keypadDialog.setCanceledOnTouchOutside(true);
            keypadDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

            TextView keypadDialogTextView = keypadDialog.findViewById(R.id.keypadDialogTextView);
            ImageButton keypadCutBtn = keypadDialog.findViewById(R.id.keypadCutBtn);
            assert keypadCutBtn != null;
            assert keypadDialogTextView != null;

            keypadCutBtn.setOnClickListener(v1 -> {
                String currentText = keypadDialogTextView.getText().toString();
                if (currentText.length() > 0) {
                    keypadDialogTextView.setText(currentText.substring(0, currentText.length() - 1));
                } else {
                    keypadDialog.cancel();
                }
            });

            FloatingActionButton getCallBottomSheet = keypadDialog.findViewById(R.id.get_call);

            getCallBottomSheet.setOnClickListener(v12 -> {
                makeCall(initBottomSheetBtns(keypadDialog, keypadDialogTextView), MainActivity.this);
                keypadDialog.cancel();
            });

            initBottomSheetBtns(keypadDialog, keypadDialogTextView);

            keypadDialog.show();
        });

        binding.more.setOnClickListener(MainActivity.this::showPopupMenu);

    }

    @Override
    protected Activity getactivity() {
        return this;
    }

    private void PhoneNumberDialog() {
        final Dialog dialog = new Dialog(this, R.style.CustomDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_phone_number);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.findViewById(R.id.tv_btn).setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(MainActivity.this, MyAccountActivity.class));
        });

        dialog.findViewById(R.id.close).setOnClickListener(v -> finishAffinity());

        dialog.show();
    }


    @SuppressLint("SetTextI18n")
    private String initBottomSheetBtns(BottomSheetDialog keypadDialog, TextView keypadDialogTextView) {
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
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "0");
        });

        btn0.setOnLongClickListener(v -> {
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "+");
            return true;
        });

        btn01.setOnClickListener(v -> {
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "1");
        });
        btn02.setOnClickListener(v -> {
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "2");
        });
        btn03.setOnClickListener(v -> {
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "3");
        });
        btn04.setOnClickListener(v -> {
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "4");
        });
        btn05.setOnClickListener(v -> {
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "5");
        });
        btn06.setOnClickListener(v -> {
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "6");
        });
        btn07.setOnClickListener(v -> {
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "7");
        });
        btn08.setOnClickListener(v -> {
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "8");
        });
        btn09.setOnClickListener(v -> {
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "9");
        });
        btnStar.setOnClickListener(v -> {
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "*");
        });
        btnHash.setOnClickListener(v -> {
            String keypadDialogTextViewText = keypadDialogTextView.getText().toString();
            keypadDialogTextView.setText(keypadDialogTextViewText + "#");
        });

        return keypadDialogTextView.getText().toString();
    }


    public void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, fragment)
                    .commit();
        }
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void filterContacts(String query) {
        filteredContactList.clear();
        databaseFilterContactList.clear();
        onlineContactList.clear();

        if (TextUtils.isEmpty(query)) {
            binding.searchItem.setVisibility(View.GONE);
        } else {
            binding.searchItem.setVisibility(View.VISIBLE);
            binding.whocallerSearch.setText("Search \"" + query + "\" on Whocaller");
            String lowerCaseQuery = query.toLowerCase();
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            String normalizedQuery = pattern.matcher(Normalizer.normalize(lowerCaseQuery, Normalizer.Form.NFD)).replaceAll("");

            for (Contact contact : contactList) {
                String name = contact.getName().toLowerCase();
                String phone = contact.getPhoneNumber();

                String normalizedName = pattern.matcher(Normalizer.normalize(name, Normalizer.Form.NFD)).replaceAll("");

                if (normalizedName.contains(normalizedQuery) || phone.contains(query)) {
                    filteredContactList.add(contact);
                }
            }

            for (Contact contact2 : databaseContactList) {
                String name = contact2.getName().toLowerCase();
                String phone = contact2.getPhoneNumber();

                String normalizedName = pattern.matcher(Normalizer.normalize(name, Normalizer.Form.NFD)).replaceAll("");

                if (normalizedName.contains(normalizedQuery) || phone.contains(query)) {
                    databaseFilterContactList.add(contact2);
                }
            }
        }

        contactAdapter.notifyDataSetChanged();
        databaseContactAdapter.notifyDataSetChanged();
    }

    public void backPressed() {
        if (binding.searchLay.getVisibility() == View.VISIBLE) {
            binding.searchLay.setVisibility(View.GONE);
            binding.flFragment.setVisibility(View.VISIBLE);
            binding.bottomNavigationView.setVisibility(View.VISIBLE);
            binding.appbar.setVisibility(View.VISIBLE);
            binding.dialPad.setVisibility(View.VISIBLE);
        } else if (binding.buttonBar.getVisibility() == View.VISIBLE) {
            binding.buttonBar.setVisibility(View.GONE);
            binding.appbar.setVisibility(View.VISIBLE);
            closebutton();
        } else {
            if (Ads.InterAddLoad) {
                ads.showInterstitialAd(this);

            } else {
                finishAffinity();
            }

        }


    }


    @Override
    public void onSelectionModeChange(boolean enabled) {
        binding.buttonBar.setVisibility(enabled ? View.VISIBLE : View.GONE);
        if (enabled) {
            binding.appbar.setVisibility(View.GONE);
        } else {
            binding.appbar.setVisibility(View.VISIBLE);
            binding.searchItem.setVisibility(View.GONE);
        }
        binding.close.setOnClickListener(v -> closebutton());

    }


    void closebutton() {
        if (adapter.multiSelectMode) {
            adapter.multiSelectMode = false;
            binding.buttonBar.setVisibility(View.GONE);
            binding.appbar.setVisibility(View.VISIBLE);
            adapter.deselectAll();
            loadFragment(new CallLogFragment());
        }
    }

    @Override
    public void onItemSelectionChange(int count) {
        if (count > 0) {
            binding.itemCount.setVisibility(View.VISIBLE);
            binding.itemCount.setText(String.valueOf(count));
        } else {
            binding.itemCount.setVisibility(View.GONE);
        }

    }

    @Override
    public void onItemDeleteChange(boolean deleted) {
        if (deleted) {
            closebutton();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (keypadDialog != null) {
            if (keypadDialog.isShowing()) {
                keypadDialog.dismiss();
            }
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());


        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.ada_new_contact) {
                openContactCreatePage();
                return true;
            } else if (id == R.id.setting) {
                if (Ads.InterAddLoad) {
                    ads.showInterstitialAd(this);
                }
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            } else if (id == R.id.search) {
                if (Ads.InterAddLoad) {
                    ads.showInterstitialAd(this);

                }
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            } else if (id == R.id.call_history) {
                if (Ads.InterAddLoad) {
                    ads.showInterstitialAd(this);

                }
                startActivity(new Intent(this, AllCallHistoryActivity.class));
                return true;
            } else {
                return false;
            }
        });

        popupMenu.show();
    }

    private void openContactCreatePage() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No app found to create contact", Toast.LENGTH_SHORT).show();
        }
    }

}