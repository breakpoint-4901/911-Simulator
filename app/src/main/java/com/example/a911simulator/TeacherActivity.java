package com.example.a911simulator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class TeacherActivity extends AppCompatActivity {

    private String displayName;
    private String contactName;
    private String contactIp;
    private InetAddress broadcastIP;
    private ContactManager contactManager;
    private ConnectActivity d;

    //used for listener
    private boolean LISTEN = false;
    private static final int LISTENER_PORT = 50003;
    private static final int BUF_SIZE = 1024;
    private boolean IN_CALL = false;
    private boolean STARTED = false;

    //logging
    static final String LOG_TAG = "TeacherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        Intent intent = getIntent();
        broadcastIP = (InetAddress)intent.getSerializableExtra(ConnectActivity.BROADCAST);

        //pull the display name from the previous intent.
        displayName = intent.getStringExtra(ConnectActivity.CONTACT_DISPLAYNAME);

        TextView deviceName = findViewById(R.id.deviceIdentity);
        deviceName.setText(displayName + "");
        contactManager = new ContactManager(displayName, broadcastIP, getBaseContext());

        STARTED = true;
        //start the listener
        startCallListener();

        //return home button
        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //creates an intent for the ConnectActivity class
                //do we need to close anything? destroy current operation for memory management?
                Intent connect = new Intent(TeacherActivity.this, HomeActivity.class);
                startActivity(connect);
            }
        });
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
                                Log.i(LOG_TAG, "RING RING:");

                                IN_CALL = true;

                                finish();
                                Intent intent = new Intent(getApplicationContext(), ReceiveCallActivity.class);
                                intent.putExtra(ConnectActivity.CONTACT_NAME, name);
                                intent.putExtra(ConnectActivity.CONTACT_IP, address.substring(1));
                                intent.putExtra(ConnectActivity.CONTACT_DISPLAYNAME, displayName);
                                intent.putExtra(ConnectActivity.BROADCAST, broadcastIP);
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
    protected void onPause() {
        super.onPause();
        if(STARTED) {

            contactManager.bye(displayName);
            contactManager.stopBroadcasting();
            contactManager.stopListening();
            //STARTED = false;
        }
       // stopCallListener();
        Log.i(LOG_TAG, "App paused!");
    }

    @Override
    protected void onStop() {
        //tells the broadcaster the device is no longer available.
        super.onStop();
        if(STARTED) {

            contactManager.bye(displayName);
            contactManager.stopBroadcasting();
            contactManager.stopListening();
            //STARTED = false;
        }
        Log.i(LOG_TAG, "App stopped!");
        //stopCallListener();
        if(!IN_CALL) {
            finish();
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "App restarted!");
        IN_CALL = false;
        STARTED = true;
        contactManager = new ContactManager(displayName, broadcastIP, getBaseContext());
        startCallListener();
    }
    @Override
    public void onBackPressed() {
        //do nothing.
    }
}
