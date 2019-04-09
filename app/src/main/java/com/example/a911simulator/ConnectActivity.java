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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;


public class ConnectActivity extends AppCompatActivity {

    static final String LOG_TAG = "UDPchat";
    private ContactManager contactManager;
    private String displayName;
    private String role;
    private InetAddress broadcastIP;
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
        broadcastIP = getBroadcastIp();
        if("teacher".equals(role)) {
            finish();
            Intent tIntent = new Intent(ConnectActivity.this, TeacherActivity.class);
            tIntent.putExtra(CONTACT_DISPLAYNAME, displayName);
            tIntent.putExtra(BROADCAST, broadcastIP);
            startActivity(tIntent);
        }

        // START searching upon activity creation
        startPeerSearch();

        //return home button
        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
                //creates an intent for the ConnectActivity class
                //do we need to close anything? destroy current operation for memory management?
                Intent connect = new Intent(ConnectActivity.this, HomeActivity.class);
                startActivity(connect);
            }
        });


        RadioGroup contactList = findViewById(R.id.contactList);
        contactList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
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
                    IN_CALL = true;

                    // Send this information to the ScenarioGenerator and start that activity
                    String address = ip.toString();
                    address = address.substring(1);
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

    private void startPeerSearch() {
        Log.i(LOG_TAG, "Start searching for devices");

        STARTED = true;

        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.VISIBLE);
        contactManager = new ContactManager(displayName, broadcastIP);

        //update our contacts [scrollView] every 5 seconds
        handler = new Handler();
        final int delay = 5000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){
                Log.i(LOG_TAG, "Updating contacts list");
                updateContactList();
                handler.postDelayed(this, delay);
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

    //getBroadcastIp helper
    private int getNetmask(int ipAddress) {
        try {

            // Convert device IP address to InetAddress class
            byte[] quads = new byte[4];
            for (int k = 0; k < 4; k++)
                quads[k] = (byte) (ipAddress >> (k * 8));
            InetAddress inetAddress = InetAddress.getByAddress(quads);

            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);

            // Get Network Prefix
            int netPrefix = 0;
            for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                netPrefix = (int)address.getNetworkPrefixLength();
            }

            // Convert Prefix to Mask
            int mask = 0xffffffff << (32 - netPrefix);
            return Integer.reverseBytes(mask);
        }
        catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
            return -1;
        }

    }
    private InetAddress getBroadcastIp() {
        // Function to return the broadcast address, based on the IP address of the device
        try {

            WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
            DhcpInfo dhcp = wifi.getDhcpInfo();

            // Get device IP address
            int ipAddress = dhcp.ipAddress;
            int netMask = getNetmask(ipAddress);

            int broadcast = (ipAddress & netMask) | ~netMask;
            byte[] quads = new byte[4];
            for (int k = 0; k < 4; k++)
                quads[k] = (byte) (broadcast >> (k * 8));
            InetAddress out = InetAddress.getByAddress(quads);
            return out;
        }
        catch(UnknownHostException e) {

            Log.e(LOG_TAG, "UnknownHostException in getBroadcastIP: " + e);
            return null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(STARTED) {
            contactManager.bye(displayName);
            contactManager.stopBroadcasting();
            contactManager.stopListening();
            //STARTED = false;
        }
        handler.removeCallbacksAndMessages(null);
        Log.i(LOG_TAG, "App paused!");
    }

    @Override
    public void onStop() {
        super.onStop();

        if(STARTED) {
            contactManager.bye(displayName);
            contactManager.stopBroadcasting();
            contactManager.stopListening();
            //STARTED = false;
        }
        if(!IN_CALL) {
            finish();
        }
        handler.removeCallbacksAndMessages(null);
        Log.i(LOG_TAG, "App stopped!");
    }

    @Override
    public void onRestart() {

        super.onRestart();

        IN_CALL = false;
        STARTED = true;
        contactManager = new ContactManager(displayName, getBroadcastIp());
        handler = new Handler();

        //restart our handler
        final int delay = 5000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){
                Log.i(LOG_TAG, "Updating contacts list");
                updateContactList();
                handler.postDelayed(this, delay);
            }
        }, delay);
        Log.i(LOG_TAG, "App restarted!");
    }
}