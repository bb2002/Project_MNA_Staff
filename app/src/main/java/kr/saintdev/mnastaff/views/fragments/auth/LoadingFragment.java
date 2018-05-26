package kr.saintdev.mnastaff.views.fragments.auth;

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
 * @date 2018-05-26
 */

public class LoadingFragment extends SuperFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmn_auth_loading, container, false);
        return v;
    }
}
