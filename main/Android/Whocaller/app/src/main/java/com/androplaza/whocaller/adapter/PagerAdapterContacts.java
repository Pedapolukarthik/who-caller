/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.androplaza.whocaller.tabsFragments.IdentifiedTabFragment;
import com.androplaza.whocaller.tabsFragments.SavedTabFragment;

public class PagerAdapterContacts extends FragmentStateAdapter {
    public PagerAdapterContacts(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SavedTabFragment();
            case 1:
                return new IdentifiedTabFragment();
            default:
                return new SavedTabFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

