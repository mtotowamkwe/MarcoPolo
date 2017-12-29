package com.merchant.explorer.marcopolo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EntrySplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_splash_screen);

        Thread marcoPoloThread = new Thread() {
            public void run() {
                try {
                    sleep(2500);
                    startActivity(new Intent(EntrySplashScreenActivity.this, MarcoPoloStoreFrontActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        marcoPoloThread.start();
    }
}
