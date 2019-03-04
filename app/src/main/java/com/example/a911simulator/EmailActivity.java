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
import android.widget.TextView;
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
                    //reformat email address into structure intents understand (array of strings)
                    String[] addresses = new String[]{email};

                    //retrieve assets
                    AssetManager assetManager = getAssets();
                    try{
                        //grab filename from strings.xml
                        String filename = getString(R.string.survey_file_name);

                        //open PDF
                        InputStream in = assetManager.open(filename);

                        //grab survey file
                        File surveyFile = streamToFile(in);

                        //create email intent and set necessary fields
                        Intent emailIntent = emailIntent(addresses);

                        try{
                            //grab file Uri
                            Uri surveyUri = FileProvider.getUriForFile(EmailActivity.this, BuildConfig.APPLICATION_ID + ".com.example.a911simulator", surveyFile);

                            //attach survey and subject to intent
                            emailIntent.putExtra(Intent.EXTRA_STREAM, surveyUri);
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "9-1-1 Sim");

                            //send email
                            try{
                                startActivity(Intent.createChooser(emailIntent, "Send mail..."));

                                //handle correct email sending

                                //clear text
                                emailEditText.setText("", TextView.BufferType.EDITABLE);

                                //send to home (unnecessary b.c. we may want multiple emails sent out)
                                //causes email intent to never showup
                                //Intent homeIntent = new Intent(EmailActivity.this, HomeActivity.class);
                                //startActivity(homeIntent);
                            }
                            catch(android.content.ActivityNotFoundException ex) {
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
                }
                else{
                    //create alert dialog
                    DialogFragment alert = new InvalidEmailDialogFragment();

                    //show alert
                    alert.show(getSupportFragmentManager(), "Error");
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

    //converts an input stream into an intent-ready file
    public File streamToFile(InputStream in) throws IOException {
        final File tempFile = File.createTempFile("survey", ".pdf", EmailActivity.this.getExternalCacheDir());

        tempFile.deleteOnExit();

        FileOutputStream out = new FileOutputStream(tempFile);

        IOUtils.copy(in, out);

        return tempFile;
    }

    //creates an intent to handle emails and passes-in the appropriate 'TO' address
    public Intent emailIntent(String[] addresses){
        //create email intent
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);

        //set intent type as email
        emailIntent.setType("text/plain");

        //brings app back after done sending email
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);

        //grant permission to read any attachments
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return emailIntent;
    }
}
