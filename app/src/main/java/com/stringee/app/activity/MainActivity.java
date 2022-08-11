package com.stringee.app.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.stringee.app.R.layout;
import com.stringee.app.common.Constant;
import com.stringee.app.common.PrefUtils;
import com.stringee.app.common.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        String token = PrefUtils.getInstance(this).getString(Constant.PREF_TOKEN, "");
        if (Utils.isTextEmpty(token)) {
            startActivity(new Intent(this, ConnectActivity.class));
        }
    }
}