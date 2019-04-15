package com.example.a911simulator;

import android.os.Handler;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;


public class ConnectActivity extends AppCompatActivity {

    static final String LOG_TAG = "ConnectActivity";
    private ContactManager contactManager;
    private String displayName;
    private String role;
    private InetAddress deviceIP;
    private boolean STARTED = false;
    private boolean IN_CALL = false; //i believe th is is just used for the ON_STOP and restart methods.

    Handler handler;

    //used for passing values between intents.
    public final static String BROADCAST = "BROADCAST";
    public final static String CONTACT_NAME = "CONTACT_NAME";
    public final static String CONTACT_IP = "IP_ADDRESS";
    public final static String CONTACT_DISPLAYNAME = "PHONE_NAME";
    public final static String ROLE = "DEVICE_ROLE"; //i think this is redundant considering the

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect); //might move this under the teacher view. JUST in case.
        Random rand = new Random();

        // Set Display Name
        final TextView displayNameText = findViewById(R.id.deviceIdentity);
        displayNameText.setText(Build.MODEL + " " + Integer.toString(rand.nextInt()));
        displayName = displayNameText.getText().toString();
        displayNameText.setEnabled(false);

        //pull the device role from the previous activity [user defined]
        Intent intent = getIntent();
        role = intent.getStringExtra(HomeActivity.ROLE);

        //the teacher should never need to press a button.
        deviceIP = getThisIP();
        if("teacher".equals(role)) {
            finish();
            Intent tIntent = new Intent(ConnectActivity.this, TeacherActivity.class);
            tIntent.putExtra(CONTACT_DISPLAYNAME, displayName);
            tIntent.putExtra(BROADCAST, deviceIP);
            startActivity(tIntent);
        } else {

            // START searching upon activity creation
            startPeerSearch();

            //return home button
            Button homeButton = findViewById(R.id.homeButton);
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    //creates an intent for the ConnectActivity class
                    //do we need to close anything? destroy current operation for memory management?
                    Intent connect = new Intent(ConnectActivity.this, HomeActivity.class);
                    startActivity(connect);
                }
            });


            RadioGroup contactList = findViewById(R.id.contactList);
            contactList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    // checkedId is the RadioButton selected

                    // Collect details about the selected contact
                    if (checkedId != -1) { //when the view updates, the state "changes" and -1 represents nothing clicked.
                        Log.w(LOG_TAG, "" + checkedId);
                        RadioButton radioButton = findViewById(checkedId);
                        String contact = radioButton.getText().toString();
                        InetAddress ip = contactManager.getContacts().get(contact);

                        // Same Device, don't try to call itself
                        if (contact.equals(displayName)) {
                            // present an error message to the user
                            Log.w(LOG_TAG, "Warning: Cannot Call Yourself");
                            final AlertDialog alert = new AlertDialog.Builder(ConnectActivity.this).create();
                            alert.setTitle("Oops");
                            alert.setMessage("Cannot Call Yourself. Please Select Another Contact");
                            alert.setButton(-1, "OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    alert.dismiss();
                                }
                            });
                            alert.show();
                            return;
                        }
                        radioGroup.clearCheck();
                        IN_CALL = true;
                        STARTED = false;
                        // Send this information to the ScenarioGenerator and start that activity
                        String address = ip.toString();
                        address = address.substring(1);
                        finish();
                        Intent intent = new Intent(ConnectActivity.this, StudentScenarioActivity.class);
                        intent.putExtra(CONTACT_NAME, contact);
                        intent.putExtra(ROLE, role);
                        intent.putExtra(CONTACT_IP, address);
                        intent.putExtra(CONTACT_DISPLAYNAME, displayName);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void startPeerSearch() {
        Log.i(LOG_TAG, "Start searching for devices");

        STARTED = true;

        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.VISIBLE);
        contactManager = new ContactManager(displayName, deviceIP, getBaseContext());

        //update our contacts [scrollView] every 5 seconds
        handler = new Handler();
        final int delay = 5000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){
                if(STARTED) {
                    Log.i(LOG_TAG, "Updating contacts list .");
                    updateContactList();
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }

    private void updateContactList() {
        // Create a copy of the HashMap used by the ContactManager
        HashMap<String, InetAddress> contacts = contactManager.getContacts();
        // Create a radio button for each contact in the HashMap
        RadioGroup radioGroup = findViewById(R.id.contactList);
        radioGroup.removeAllViews();

        for (String name : contacts.keySet()) {

            RadioButton radioButton = new RadioButton(getBaseContext());
            radioButton.setText(name);
            radioButton.setTextColor(Color.BLACK);
            radioGroup.addView(radioButton);
        }

        radioGroup.clearCheck();
    }

    private InetAddress getThisIP() {
        // Function to return the IP address of the device
        try {

            WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
            DhcpInfo dhcp = wifi.getDhcpInfo();

            // Get device IP address
            int ipAddress = dhcp.ipAddress;
            byte[] quads = new byte[4];
            for (int k = 0; k < 4; k++)
                quads[k] = (byte) (ipAddress >> (k * 8));
            InetAddress IP = InetAddress.getByAddress(quads);

            return IP;
        }
        catch(UnknownHostException e) {

            Log.e(LOG_TAG, "UnknownHostException in getThisIP: " + e);
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(STARTED) {
            contactManager.bye(displayName);
            contactManager.stopBroadcasting();
            contactManager.stopListening();
            STARTED = false;
        }
        handler.removeCallbacksAndMessages(null);
        Log.i(LOG_TAG, "App paused!");
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(STARTED) {
            contactManager.bye(displayName);
            contactManager.stopBroadcasting();
            contactManager.stopListening();
            STARTED = false;
        }
        if(!IN_CALL) {
            finish();
        }
        handler.removeCallbacksAndMessages(null);
        Log.i(LOG_TAG, "App stopped!");
    }

    @Override
    protected void onRestart() {

        super.onRestart();

        IN_CALL = false;
        STARTED = true;
        contactManager = new ContactManager(displayName, getThisIP(), getBaseContext());
        handler = new Handler();

        //restart our handler
        final int delay = 5000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){
                if(STARTED) {
                    Log.i(LOG_TAG, "Updating contacts list");
                    updateContactList();
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
        Log.i(LOG_TAG, "App restarted!");
    }

    @Override
    public void onBackPressed() {
        //do nothing.
    }
}