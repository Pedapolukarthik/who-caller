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

import com.androplaza.whocaller.tabsFragments.BlockTabFragment;
import com.androplaza.whocaller.tabsFragments.SpamTabFragment;

public class PagerAdapterSpamBlock extends FragmentStateAdapter {
    public PagerAdapterSpamBlock(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SpamTabFragment();
            case 1:
                return new BlockTabFragment();
            default:
                return new SpamTabFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
