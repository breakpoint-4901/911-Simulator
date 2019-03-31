package com.example.a911simulator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class SimulatedDialpadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulated_dialpad);

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


    }
}
