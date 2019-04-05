package com.example.a911simulator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class SimulatedDialpadActivity extends AppCompatActivity {

    private String displayName;
    private String contactName;
    private String contactIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulated_dialpad);

        //get data from previous intent.
        Intent intent = getIntent();
        displayName = intent.getStringExtra(ConnectActivity.CONTACT_DISPLAYNAME);
        contactName = intent.getStringExtra(ConnectActivity.CONTACT_NAME);
        contactIp = intent.getStringExtra(ConnectActivity.CONTACT_IP);

        //set up variables
        ImageButton number1Btn = findViewById(R.id.oneImageBtn);
        ImageButton number2Btn = findViewById(R.id.twoImageBtn);
        ImageButton number3Btn = findViewById(R.id.threeImageBtn);
        ImageButton number4Btn = findViewById(R.id.fourImageBtn);
        ImageButton number5Btn = findViewById(R.id.fiveImageBtn);
        ImageButton number6Btn = findViewById(R.id.sixImageBtn);
        ImageButton number7Btn = findViewById(R.id.sevenImageBtn);
        ImageButton number8Btn = findViewById(R.id.eightImageBtn);
        ImageButton number9Btn = findViewById(R.id.nineImageBtn);
        ImageButton number0Btn = findViewById(R.id.zeroImageBtn);
        ImageButton asteriskBtn = findViewById(R.id.asteriskImageBtn);
        ImageButton poundBtn = findViewById(R.id.poundImageBtn);
        ImageButton callBtn = findViewById(R.id.callImageBtn);
        ImageButton backspaceBtn = findViewById(R.id.backspaceBtn);
        TextView numberDialedTextView = findViewById(R.id.numberTextView);

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent makeCall = new Intent(SimulatedDialpadActivity.this, MakeCallActivity.class);
                // Send this information to the SimulatedDialpad and start that activity
                makeCall.putExtra(ConnectActivity.CONTACT_NAME, contactName);
                makeCall.putExtra(ConnectActivity.CONTACT_IP, contactIp);
                makeCall.putExtra(ConnectActivity.CONTACT_DISPLAYNAME, displayName);
                startActivity(makeCall);
            }
        });
    }
}
