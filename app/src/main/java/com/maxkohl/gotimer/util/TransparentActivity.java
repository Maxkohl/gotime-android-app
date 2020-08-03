package com.maxkohl.gotimer.util;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.maxkohl.gotimer.R;

public class TransparentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);
    }
}