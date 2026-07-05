/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.tabsFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.androplaza.whocaller.adapter.OnlineContactAdapter;
import com.androplaza.whocaller.database.sqlite.ContactsDataDb;
import com.androplaza.whocaller.databinding.FragmentSpamTabBinding;
import com.androplaza.whocaller.modal.Contact;

import java.util.List;

public class SpamTabFragment extends Fragment {

    FragmentSpamTabBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSpamTabBinding.inflate(inflater, container, false);

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadContacts();


        return binding.getRoot();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadContacts() {
        ContactsDataDb databaseHelper = new ContactsDataDb(requireContext());
        List<Contact> contactList = databaseHelper.getSpamContacts();

        OnlineContactAdapter contactAdapter = new OnlineContactAdapter(contactList, requireContext());
        binding.recyclerview.setAdapter(contactAdapter);
        contactAdapter.notifyDataSetChanged();

        if (contactList.isEmpty()) {
            binding.demoItem.setVisibility(View.VISIBLE);
            binding.recyclerview.setVisibility(View.GONE);
        } else {
            binding.demoItem.setVisibility(View.GONE);
            binding.recyclerview.setVisibility(View.VISIBLE);
        }
    }


}
