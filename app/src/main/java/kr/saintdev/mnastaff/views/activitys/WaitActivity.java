package kr.saintdev.mnastaff.views.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import kr.saintdev.mnastaff.R;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-27
 */

public class WaitActivity extends AppCompatActivity {
    Button retryButton = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        getSupportActionBar().hide();

        this.retryButton = findViewById(R.id.wait_retry);
        this.retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WaitActivity.this, AuthActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }
}
