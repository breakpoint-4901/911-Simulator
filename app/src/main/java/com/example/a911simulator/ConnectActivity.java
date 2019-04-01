package com.example.a911simulator;

import android.view.View.OnClickListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;


public class ConnectActivity extends AppCompatActivity {


    static final String LOG_TAG = "UDPchat";
    private static final int LISTENER_PORT = 50003;
    private static final int BUF_SIZE = 1024;
    private ContactManager contactManager;
    private String displayName;
    private boolean STARTED = false;
    private boolean IN_CALL = false;
    private boolean LISTEN = false;

    public final static String EXTRA_CONTACT = "hw.dt83.udpchat.CONTACT";
    public final static String EXTRA_IP = "hw.dt83.udpchat.IP";
    public final static String EXTRA_DISPLAYNAME = "hw.dt83.udpchat.DISPLAYNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Random rand = new Random();

        // Set Display Name
        final EditText displayNameText = (EditText) findViewById(R.id.editTextDisplayName);
        displayNameText.setText(Build.MODEL + " " + Integer.toString(rand.nextInt()));
        // displayNameText.setText(Build.MODEL);
        displayName = displayNameText.getText().toString();
        displayNameText.setEnabled(false);

        Log.i(LOG_TAG, "UDPChat started");

        // START BUTTON
        // Pressing this buttons initiates the main functionality
        final Button btnStart = (Button) findViewById(R.id.buttonStart);
        btnStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.i(LOG_TAG, "Start button pressed");
                STARTED = true;

                btnStart.setEnabled(false);

                TextView text = (TextView) findViewById(R.id.textViewSelectContact);
                text.setVisibility(View.VISIBLE);

                Button updateButton = (Button) findViewById(R.id.buttonUpdate);
                updateButton.setVisibility(View.VISIBLE);

                Button callButton = (Button) findViewById(R.id.buttonCall);
                callButton.setVisibility(View.VISIBLE);

                ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
                scrollView.setVisibility(View.VISIBLE);

                contactManager = new ContactManager(displayName, getBroadcastIp());
                startCallListener();
            }
        });

        // UPDATE BUTTON
        // Updates the list of reachable devices
        final Button btnUpdate = (Button) findViewById(R.id.buttonUpdate);
        btnUpdate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                updateContactList();
            }
        });

        // CALL BUTTON
        // Attempts to initiate an audio chat session with the selected device
        final Button btnCall = (Button) findViewById(R.id.buttonCall);
        btnCall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.contactList);
                int selectedButton = radioGroup.getCheckedRadioButtonId();
                if (selectedButton == -1) {
                    // If no device was selected, present an error message to the user
                    Log.w(LOG_TAG, "Warning: no contact selected");
                    final AlertDialog alert = new AlertDialog.Builder(ConnectActivity.this).create();
                    alert.setTitle("Oops");
                    alert.setMessage("You must select a contact first");
                    alert.setButton(-1, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            alert.dismiss();
                        }
                    });
                    alert.show();
                    return;
                }
                // Collect details about the selected contact
                RadioButton radioButton = (RadioButton) findViewById(selectedButton);
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

                // Send this information to the MakeCallActivity and start that activity
                Intent intent = new Intent(ConnectActivity.this, MakeCallActivity.class);
                intent.putExtra(EXTRA_CONTACT, contact);
                String address = ip.toString();
                address = address.substring(1, address.length());
                intent.putExtra(EXTRA_IP, address);
                intent.putExtra(EXTRA_DISPLAYNAME, displayName);
                startActivity(intent);
            }
        });
    }

    private void updateContactList() {
        // Create a copy of the HashMap used by the ContactManager
        HashMap<String, InetAddress> contacts = contactManager.getContacts();
        // Create a radio button for each contact in the HashMap
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.contactList);
        radioGroup.removeAllViews();

        for (String name : contacts.keySet()) {

            RadioButton radioButton = new RadioButton(getBaseContext());
            radioButton.setText(name);
            radioButton.setTextColor(Color.BLACK);
            radioGroup.addView(radioButton);
        }

        radioGroup.clearCheck();
    }

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

    private void startCallListener() {
        // Creates the listener thread
        LISTEN = true;
        Thread listener = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    // Set up the socket and packet to receive
                    Log.i(LOG_TAG, "Incoming call listener started");
                    DatagramSocket socket = new DatagramSocket(LISTENER_PORT);
                    socket.setSoTimeout(1000);
                    byte[] buffer = new byte[BUF_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, BUF_SIZE);
                    while(LISTEN) {
                        // Listen for incoming call requests
                        try {
                            Log.i(LOG_TAG, "Listening for incoming calls");
                            socket.receive(packet);
                            String data = new String(buffer, 0, packet.getLength());
                            Log.i(LOG_TAG, "Packet received from "+ packet.getAddress() +" with contents: " + data);
                            String action = data.substring(0, 4);
                            if(action.equals("CAL:")) {
                                // Received a call request. Start the ReceiveCallActivity
                                String address = packet.getAddress().toString();
                                String name = data.substring(4, packet.getLength());

                                Intent intent = new Intent(ConnectActivity.this, ReceiveCallActivity.class);
                                intent.putExtra(EXTRA_CONTACT, name);
                                intent.putExtra(EXTRA_IP, address.substring(1, address.length()));
                                IN_CALL = true;
                                //LISTEN = false;
                                //stopCallListener();
                                startActivity(intent);
                            }
                            else {
                                // Received an invalid request
                                Log.w(LOG_TAG, packet.getAddress() + " sent invalid message: " + data);
                            }
                        }
                        catch(Exception e) {}
                    }
                    Log.i(LOG_TAG, "Call Listener ending");
                    socket.disconnect();
                    socket.close();
                }
                catch(SocketException e) {

                    Log.e(LOG_TAG, "SocketException in listener " + e);
                }
            }
        });
        listener.start();
    }

    private void stopCallListener() {
        // Ends the listener thread
        LISTEN = false;
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
        stopCallListener();
        Log.i(LOG_TAG, "App paused!");
    }

    @Override
    public void onStop() {

        super.onStop();
        Log.i(LOG_TAG, "App stopped!");
        stopCallListener();
        if(!IN_CALL) {

            finish();
        }
    }

    @Override
    public void onRestart() {

        super.onRestart();
        Log.i(LOG_TAG, "App restarted!");
        IN_CALL = false;
        STARTED = true;
        contactManager = new ContactManager(displayName, getBroadcastIp());
        startCallListener();
    }
}

