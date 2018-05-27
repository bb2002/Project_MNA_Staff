package kr.saintdev.mnastaff.views.fragments.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.saintdev.mnastaff.R;
import kr.saintdev.mnastaff.views.fragments.SuperFragment;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-27
 */

public class HomeFragment extends SuperFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_main_home, container, false);
        return v;
    }
}
