package com.example.a911simulator;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class EmailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        //grab necessary ui elements
        final EditText emailEditText = findViewById(R.id.emailEditText);
        Button submitEmailButton = findViewById(R.id.submitEmailButton);

        //handle email submission logic
        submitEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //grab email from editText
                String email = emailEditText.getText().toString().trim();

                //validate email
                if(isValidEmail(email)){
                    String[] addresses = new String[]{email};
                    //TODO: change out string values for string refs from strings.xml

                    //open the survey pdf
                    AssetManager assetManager = getAssets();
                    try{
                        InputStream in = assetManager.open("sample_doc.pdf");

                        //grab survey file
                        File surveyFile = streamToFile(in);

                        //create email intent
                        Intent emailIntent = new Intent();
                        emailIntent.setAction(Intent.ACTION_SEND);

                        //set intent type as email
                        emailIntent.setType("text/plain");

                        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//brings app back after done
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);

                        //grant permission
                        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        try{
                            //grab file Uri
                            Uri surveyUri = FileProvider.getUriForFile(EmailActivity.this, BuildConfig.APPLICATION_ID + ".com.example.a911simulator", surveyFile);

                            //attach survey
                            emailIntent.putExtra(Intent.EXTRA_STREAM, surveyUri);

                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "9-1-1 Sim");


                            //send email
                            try{
                                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                            }
                            catch(android.content.ActivityNotFoundException ex){
                                Toast.makeText(EmailActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch(IllegalArgumentException ex){
                            Toast.makeText(EmailActivity.this, "File is outside the paths supported by provider.", Toast.LENGTH_SHORT).show();
                        }

                    }
                    catch(java.io.IOException ex){
                        Toast.makeText(EmailActivity.this, "There file does not exist.", Toast.LENGTH_SHORT).show();
                    }

                    //create email intent

                    //failed attempts
                    /*
                    Uri uri = Uri.fromFile(new File("///android_asset/sample_doc.pdf"));
                            //Uri.parse("android.resource://com.example.a911simulator/assets/sample_doc.pdf");//Uri.fromFile(file);

                    Intent emailIntent = new Intent(Intent.ACTION_SEND,Uri.parse("mailto:"));
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My Subject");

                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

                    emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(emailIntent);*/

                    /*Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

                    //set type
                    //emailIntent.setType("text/plain");
                    emailIntent.setData(Uri.parse("mailto:"));

                    //grab pdf
                    //File survey = new File("res/raw/sample_doc.pdf");
                    //Uri surveyUri = Uri.fromFile(survey);
                    //Uri surveyUri = Uri.parse("android.resource://" + getPackage() + "/raw/sample_doc");


                    Uri surveyUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://com.example.a911simulator/assets/sample_doc");



                    //set email
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);

                    //getResources().openRawResource(R.assets.sample_doc);

                    //set content
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "9-1-1 Survey");
                    //emailIntent.putExtra(Intent.EXTRA_TEXT, "yooo whaddup");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, surveyUri);
                    //have to allow gmail to access photos/media/apps
                    //set flag
                    emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
/*
                    try{
                        //startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    }
                    catch(android.content.ActivityNotFoundException ex){
                        Toast.makeText(EmailActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }*/

                    /*File sur = getResources().
                            openRawResource(getResources().
                                    getIdentifier("sample_doc", "raw", getPackageName()));*/
                }
                else{
                    //create alert dialog
                    DialogFragment alert = new InvalidEmailDialogFragment();

                    //show alert
                    alert.show(getSupportFragmentManager(), "something");
                }


            }
        });
    }

    //special thanks to StackOverflow users user1737884 and Andrey on
    //https://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    //returns the package name (e.g. com.example.a911simulator)
    public String getPackage(){
        return getApplicationContext().getPackageName();
    }

    public File streamToFile(InputStream in) throws IOException {
        final File tempFile = File.createTempFile("sample_doc", ".pdf", EmailActivity.this.getExternalCacheDir());

        tempFile.deleteOnExit();

        FileOutputStream out = new FileOutputStream(tempFile);

        IOUtils.copy(in, out);

        return tempFile;
    }
}
