package com.sha512boo.ArizonaLauncher;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sha512boo.ArizonaLauncher.fragment.FavoriteServersFragment;
import com.sha512boo.ArizonaLauncher.fragment.HostedServersFragment;
import com.sha512boo.ArizonaLauncher.fragment.OfficialServersFragment;

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Favorites", "Official", "Hosted" };
    private Context context;

    public SampleFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override public int getCount() {
        return PAGE_COUNT;
    }

    @Override public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new FavoriteServersFragment();
            case 1:
                return new OfficialServersFragment();
            case 2:
                return new HostedServersFragment();
            default:
                return new FavoriteServersFragment();


        }
    }

    @Override public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
