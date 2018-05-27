package kr.saintdev.mnastaff.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kr.saintdev.mnastaff.views.fragments.SuperFragment;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-27
 */

public class MainActivityAdapter extends FragmentStatePagerAdapter {
    SuperFragment[] pages = null;

    public MainActivityAdapter(FragmentManager fm, SuperFragment[] pages) {
        super(fm);
        this.pages = pages;
    }

    @Override
    public Fragment getItem(int position) {
        return this.pages[position];
    }

    @Override
    public int getCount() {
        return this.pages.length;
    }
}
