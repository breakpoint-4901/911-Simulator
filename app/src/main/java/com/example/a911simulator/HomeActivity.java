package com.example.a911simulator;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //grab buttons and put into variables
        Button getStartedButton = findViewById(R.id.getStartedButton);
        Button settingsButton = findViewById(R.id.settingsButton);
        Button surveyButton = findViewById(R.id.surveyButton);

        /*
         * create listeners for starting, settings, and survey button
         * each listener opens up a new activity
         * */

        getStartedButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //creates an intent for the ConnectActivity class
                Intent connect = new Intent(HomeActivity.this, ConnectActivity.class);

                startActivity(connect);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //creates an intent for the SettingsActivity class
                Intent settings = new Intent(HomeActivity.this, SettingsActivity.class);

                startActivity(settings);
            }
        });

        surveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creates an intent for the EmailActivity class
                Intent email = new Intent(HomeActivity.this, EmailActivity.class);

                startActivity(email);
            }
        });
    }
}
