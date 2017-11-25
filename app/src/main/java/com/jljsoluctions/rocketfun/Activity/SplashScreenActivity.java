package com.jljsoluctions.rocketfun.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.jljsoluctions.rocketfun.Class.Useful;
import com.jljsoluctions.rocketfun.R;

/**
 * Created by jever on 11/07/2017.
 */

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Useful.firebasePersistenceCalledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Useful.firebasePersistenceCalledAlready = true;
        }
        setContentView(R.layout.activity_splash_screen);
        Handler handle = new Handler();

        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                //showMainActivity();
                finish();
        }
        }, 3000);

    }

    private void showMainActivity() {

        Intent intent = new Intent(SplashScreenActivity.this,
                MainActivity.class);

        startActivity(intent);

    }

}