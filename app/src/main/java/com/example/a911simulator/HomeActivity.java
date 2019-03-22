package com.example.a911simulator;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    final String [] supportedLanguages = {"English","Español"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_home);

        //grab buttons and put into variables
        Button getStartedButton = findViewById(R.id.getStartedButton);
        Button settingsButton = findViewById(R.id.settingsButton);
        Button surveyButton = findViewById(R.id.surveyButton);

        /*
         * create listeners for starting, changing languages, and survey buttons
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

        //opens a dialog box that allows the user to change languages
        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //shows AlertDialog to display list of languages, only one can be selected
                showChangeLanguageDialog();
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

    private void showChangeLanguageDialog(){


        //create alert dialog and pass in list of languages
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
        mBuilder.setTitle(R.string.choose_language);

        mBuilder.setSingleChoiceItems(supportedLanguages, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //set locale
                if(which == 0){
                    setLocale("en");
                }
                else if(which == 1){
                    setLocale("es");
                }

                //recreate view
                recreate();

                //dismiss alert dialog
                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        //show alert dialog
        mDialog.show();
    }

    //sets the locale
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        //deprecated code but does not require app restart
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //non-deprecated code but requires app restart and debugging to find why it crashes
        /*Configuration config = new Configuration();
        config.setLocale(locale);
        getApplicationContext().createConfigurationContext(config);*/

        //save data to shared preferences
        saveLocale(lang);
    }

    //saves locale language to shared preferences
    private void saveLocale(String lang){
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    //load language saved in shared preferences
    public void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        setLocale(language);
    }
}
