package com.example.chatup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;


public class IntroPage extends AppCompatActivity {
    public static final int splash_screen_timer = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_page);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroPage.this, PhoneAuthentication.class);
                startActivity(intent);
            }
        }, splash_screen_timer);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }
}
