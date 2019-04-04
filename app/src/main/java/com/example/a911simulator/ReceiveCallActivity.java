package com.example.a911simulator;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
//TEACHER
public class ReceiveCallActivity extends AppCompatActivity {
    private static final String LOG_TAG = "ReceiveCall";
    private static final int BROADCAST_PORT = 50002;
    private static final int BUF_SIZE = 1024;
    private String contactIp;
    private String contactName;
    private boolean LISTEN = true;
    private boolean IN_CALL = false;
    private AudioCall call;

    LayoutInflater inflater;
    View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_call);

        inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //initialize our layout
        ConstraintLayout layout=findViewById(R.id.parentLayout); //grab the container for our current view.
        view=inflater.inflate(R.layout.activity_answer_call,layout, false);  //convert our XML into an object
        layout.addView(view); //import the xml into our activity

        view.setVisibility(ConstraintLayout.GONE); //hide the XML for when a user accepts a call.


        Intent intent = getIntent();
        contactName = intent.getStringExtra(ConnectActivity.EXTRA_CONTACT);
        contactIp = intent.getStringExtra(ConnectActivity.EXTRA_IP);

        TextView textView = findViewById(R.id.textViewCallerID);
        TextView textDate = findViewById(R.id.recieveCallDate);
        String curDate = getMMDD();

        textDate.setText(curDate); //sets our placeholder date to the current date. (does not update if it hits 12:00am
        textView.setText(""+contactName); //ensures we treat our object as a string (prevents NULL from crashing)

        startListener();

        // ACCEPT BUTTON
        ImageView acceptButton = findViewById(R.id.buttonAccept);
        acceptButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    // Accepting call. Send a notification and start the call
                    sendMessage("ACC:");
                    InetAddress address = InetAddress.getByName(contactIp);
                    Log.i(LOG_TAG, "Calling " + address.toString());
                    IN_CALL = true;
                    call = new AudioCall(address);
                    call.startCall();
                    // Hide the buttons as they're not longer required
                    ImageView accept = findViewById(R.id.buttonAccept);
                    accept.setEnabled(false);

                    ImageView reject = findViewById(R.id.buttonReject);
                    reject.setEnabled(false);

                    //change the layout
                    answerCallXML();
                }
                catch(UnknownHostException e) {

                    Log.e(LOG_TAG, "UnknownHostException in acceptButton: " + e);
                }
                catch(Exception e) {

                    Log.e(LOG_TAG, "Exception in acceptButton: " + e);
                }
            }
        });

        // REJECT BUTTON
        ImageView rejectButton = findViewById(R.id.buttonReject);
        rejectButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Send a reject notification and end the call
                sendMessage("REJ:");
                endCall();

                //move to revert to a previous intent when a call is rejected. TODO Change this logic (where do we want the user to be taken)
                Intent connect = new Intent(ReceiveCallActivity.this, HomeActivity.class);
                startActivity(connect);
            }
        });

        // END BUTTON
        ImageView endButton = findViewById(R.id.buttonEndCall1);
        endButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.i(LOG_TAG, "Student hangup ");
                //move to revert to a previous intent after a call ends. TODO Change this logic (where do we want the user to be taken)
                endCall();
                Intent connect = new Intent(ReceiveCallActivity.this, HomeActivity.class);
                startActivity(connect);
            }
        });
    }

    private void answerCallXML() {

        View receive= findViewById(R.id.receiveView); //grab view from XML
        receive.setVisibility(ConstraintLayout.GONE); //banish the activity_receive_call.xml
        view.setVisibility(LinearLayout.VISIBLE); // bring the activity_answer_call.xml to the foreground.

        TextView textContactName = findViewById(R.id.makeCallContactName);
        textContactName.setText(""+contactName);

        TextView textContactIP = findViewById(R.id.makeCallipAddress);
        textContactIP.setText(""+contactIp);

        //start an async function call for changing the time on our textview
    }

    //returns a string formatted as: "MM DD" or "Feb 17"
    private String getMMDD() {
        Date date = new Date();
        String mmdd = "MMM dd"; //the format we wish to return
        String result;

        SimpleDateFormat sdf = new SimpleDateFormat(mmdd); //pass in our desired format
        result = sdf.format(date); //formats the information using the current date

        return result;
    }

    private void endCall() {
        // End the call and send a notification
        stopListener();
        if(IN_CALL) {
            call.endCall();
        }
        sendMessage("END:");
        finish();
    }

    private void startListener() {
        // Creates the listener thread
        LISTEN = true;
        Thread listenThread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    Log.i(LOG_TAG, "Listener started!");
                    DatagramSocket socket = new DatagramSocket(BROADCAST_PORT);
                    socket.setSoTimeout(1500);
                    byte[] buffer = new byte[BUF_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, BUF_SIZE);
                    while(LISTEN) {

                        try {

                            Log.i(LOG_TAG, "Listening for packets");
                            socket.receive(packet);
                            String data = new String(buffer, 0, packet.getLength());
                            Log.i(LOG_TAG, "Packet received from "+ packet.getAddress() +" with contents: " + data);
                            String action = data.substring(0, 4);
                            if(action.equals("END:")) {
                                // End call notification received. End call
                                endCall();
                            }
                            else {
                                // Invalid notification received.
                                Log.w(LOG_TAG, packet.getAddress() + " sent invalid message: " + data);
                            }
                        }
                        catch(IOException e) {

                            Log.e(LOG_TAG, "IOException in Listener " + e);
                        }
                    }
                    Log.i(LOG_TAG, "Listener ending");
                    socket.disconnect();
                    socket.close();
                    return;
                }
                catch(SocketException e) {

                    Log.e(LOG_TAG, "SocketException in Listener " + e);
                    endCall();
                }
            }
        });
        listenThread.start();
    }

    private void stopListener() {
        // Ends the listener thread
        LISTEN = false;
    }

    private void sendMessage(final String message) {
        // Creates a thread for sending notifications
        Thread replyThread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    InetAddress address = InetAddress.getByName(contactIp);
                    byte[] data = message.getBytes();
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(data, data.length, address, BROADCAST_PORT);
                    socket.send(packet);
                    Log.i(LOG_TAG, "Sent message( " + message + " ) to " + contactIp);
                    socket.disconnect();
                    socket.close();
                }
                catch(UnknownHostException e) {

                    Log.e(LOG_TAG, "Failure. UnknownHostException in sendMessage: " + contactIp);
                }
                catch(SocketException e) {

                    Log.e(LOG_TAG, "Failure. SocketException in sendMessage: " + e);
                }
                catch(IOException e) {

                    Log.e(LOG_TAG, "Failure. IOException in sendMessage: " + e);
                }
            }
        });
        replyThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.receive_call, menu);
        return true;
    }

}
