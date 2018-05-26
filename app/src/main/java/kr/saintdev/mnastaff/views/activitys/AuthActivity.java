package kr.saintdev.mnastaff.views.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import kr.saintdev.mnastaff.R;
import kr.saintdev.mnastaff.views.fragments.SuperFragment;
import kr.saintdev.mnastaff.views.fragments.auth.LoadingFragment;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @date 2018-05-26
 */

public class AuthActivity extends AppCompatActivity {
    SuperFragment nowFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        switchFragment(new LoadingFragment());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.nowFragment.onActivityResult(requestCode, resultCode, data);
    }

    public void switchFragment(SuperFragment view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.auth_container, view);
        ft.commit();

        this.nowFragment = view;
    }

    public void setActionBarTitle(@Nullable String title) {
        ActionBar bar = getSupportActionBar();
        if(bar != null) {
            if(title == null) {
                bar.hide();
            } else {
                bar.show();
                bar.setTitle(title);
            }
        }
    }
}
