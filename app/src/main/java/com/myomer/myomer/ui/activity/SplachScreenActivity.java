package com.myomer.myomer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.myomer.myomer.R;
import com.myomer.myomer.helpers.SharedPreferenceHelper;
import com.myomer.myomer.utilty.Constants;
import com.myomer.myomer.utilty.Utilty;

public class SplachScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splach_screen);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Utilty.resetNotifications(SplachScreenActivity.this);
                if (SharedPreferenceHelper.getSharedPreferenceBoolean(SplachScreenActivity.this, Constants.FIRSTIME_USER, true)) {
                    Intent i = new Intent(SplachScreenActivity.this, com.myomer.myomer.activities.UserFormActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(SplachScreenActivity.this, com.myomer.myomer.activities.WaitingScreenActivity.class);
                    startActivity(i);
                }
                // close this activity
                finish();

            }
        }, 3000);
    }


}
