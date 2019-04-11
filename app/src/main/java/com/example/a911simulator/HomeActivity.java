package com.example.a911simulator;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private final String [] supportedLanguages = {"English","Español"};
    String [] supportedRoles = {};
    PermissionManager permissionManager; //used to requesting/checking permissions

    public final static String ROLE = "ROLE"; //used for passing in for future states

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_home);

        //grab buttons and put into variables
        Button getStartedButton = findViewById(R.id.getStartedButton);
        Button settingsButton = findViewById(R.id.settingsButton);
        final Button surveyButton = findViewById(R.id.surveyButton);

        //increases the 'hit area' for the survey button
        increaseHitAreaOfBy(surveyButton, 30);

        //prompt our users to accept the permissions when the app is first started.
        permissionManager = new PermissionManager() { //declare any override functions if the defaults are not to expectations
           @Override
           public void ifCancelledAndCannotRequest(Activity activity) { //user clicked DENIED
               permissionsAlert(); //popup box which informs user they need to accept the permissions
           }
           @Override
           public void ifCancelledAndCanRequest(Activity activity) { //user clicked no ask later
               // dialog box informing user the permissions are required
               permissionsAlert(); //popup box which informs user they need to accept the permissions
           }
        };

        //this does the process of actually requesting the permissions
        permissionManager.checkAndRequestPermissions(this);

        //create listener for simulation start
        getStartedButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //creates an intent for the ConnectActivity class
                ArrayList<String> denied = permissionManager.getStatus().get(0).denied; //queries a list of denied permissions
                if( denied.size() == 0) {
                    showRoleDialog();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please enable the permissions under app preferences.",Toast.LENGTH_LONG).show();
                }

            }
        });

        //create listener for survey sending
        surveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creates an intent for the EmailActivity class
                Intent email = new Intent(HomeActivity.this, EmailActivity.class);

                startActivity(email);
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
    }

    //takes in a button and increases its 'hit box' for ui tapping
    private void increaseHitAreaOfBy(final Button btn, final int delta){
        final View parent = (View) btn.getParent();
        parent.post(new Runnable() {
            @Override
            public void run() {
                //create new rectangular hit box
                final Rect rect = new Rect();
                btn.getHitRect(rect);

                //increase bounds of shape
                rect.top -= delta;
                rect.left -= delta;
                rect.right += delta;
                rect.bottom += delta;

                //set button 'hit area' as new rect
                parent.setTouchDelegate( new TouchDelegate(rect, btn));
            }
        });
    }

    private void permissionsAlert() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this, R.style.DialogTheme);
        builder1.setMessage("Specific permissions are required to use the simulation. Please enable them in app preferences under system settings.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

//        builder1.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        permissionManager.checkResult(requestCode, permissions, grantResults);

        //useful for logging
        ArrayList<String> granted = permissionManager.getStatus().get(0).granted; //a container holding all of the granted permissions for this application
        ArrayList<String> denied = permissionManager.getStatus().get(0).denied; //a container holding all of the granted permissions for this application
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

    private void showRoleDialog(){
        //create alert dialog and pass in list of languages
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
        mBuilder.setTitle(R.string.choose_role);

        supportedRoles = getResources().getStringArray(R.array.device_roles);

        mBuilder.setSingleChoiceItems(supportedRoles, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //set locale
                Intent connect = new Intent(HomeActivity.this, ConnectActivity.class);
                if(which == 0){
                    connect.putExtra(ROLE, "teacher");
                    startActivity(connect);
                }
                else if(which == 1){ //TODO Remove this part
                    connect.putExtra(ROLE, "student");
                    startActivity(connect);
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
    private void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        setLocale(language);
    }
}
