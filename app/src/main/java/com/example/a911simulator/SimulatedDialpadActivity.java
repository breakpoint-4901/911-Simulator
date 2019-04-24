package com.example.a911simulator;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SimulatedDialpadActivity extends AppCompatActivity {

    private String displayName;
    private String contactName;
    private String contactIp;

    //class objects to allow for method functions to access without passing-in as params
    private ImageButton number1Btn;
    private ImageButton number2Btn;
    private ImageButton number3Btn;
    private ImageButton number4Btn;
    private ImageButton number5Btn;
    private ImageButton number6Btn;
    private ImageButton  number7Btn;
    private ImageButton number8Btn;
    private ImageButton number9Btn;
    private ImageButton number0Btn;
    private ImageButton asteriskBtn;
    private ImageButton poundBtn;
    private ImageButton callBtn;
    private ImageButton backspaceBtn;

    //private String dialedNumber;
    //create an animation
    private AlphaAnimation blinkAnimation;
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
        number1Btn = findViewById(R.id.oneImageBtn);
        number2Btn = findViewById(R.id.twoImageBtn);
        number3Btn = findViewById(R.id.threeImageBtn);
        number4Btn = findViewById(R.id.fourImageBtn);
        number5Btn = findViewById(R.id.fiveImageBtn);
        number6Btn = findViewById(R.id.sixImageBtn);
        number7Btn = findViewById(R.id.sevenImageBtn);
        number8Btn = findViewById(R.id.eightImageBtn);
        number9Btn = findViewById(R.id.nineImageBtn);
        number0Btn = findViewById(R.id.zeroImageBtn);
        asteriskBtn = findViewById(R.id.asteriskImageBtn);
        poundBtn = findViewById(R.id.poundImageBtn);
        callBtn = findViewById(R.id.callImageBtn);
        backspaceBtn = findViewById(R.id.backspaceBtn);
        final TextView numberDialedTextView = findViewById(R.id.numberTextView);

        //setup animation
        blinkAnimation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        blinkAnimation.setDuration(300); // duration - half a second
        blinkAnimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        blinkAnimation.setRepeatCount(2); // Repeat animation infinitely
        blinkAnimation.setRepeatMode(Animation.REVERSE);

        number1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "1")){
                    clearAnimations();
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "1"));

            }
        });

        number2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "2")){
                    clearAnimations();
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "2"));
            }
        });

        number3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "3")){
                    clearAnimations();
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "3"));
            }
        });

        number4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "4")){
                    clearAnimations();
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "4"));
            }
        });

        number5Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "5")){
                    clearAnimations();
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "5"));
            }
        });

        number6Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "6")){
                    clearAnimations();
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "6"));
            }
        });

        number7Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "7")){
                    clearAnimations();
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "7"));
            }
        });

        number8Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "8")){
                    clearAnimations();
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "8"));
            }
        });

        number9Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "9")){
                    clearAnimations();
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "9"));
            }
        });

        number0Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "0")){
                    clearAnimations();
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "0"));
            }
        });

        asteriskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "*")){
                    clearAnimations();
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "*"));
            }
        });

        poundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "#")){
                    clearAnimations();
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "#"));
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "call")){
                    clearAnimations();
                    switch (getTextFromTextView(numberDialedTextView)){
                        case "":
                            number9Btn.startAnimation(blinkAnimation);
                            break;
                        case "9":
                        case "91":
                            number1Btn.startAnimation(blinkAnimation);
                            break;
                        case "911":
                            callBtn.startAnimation(blinkAnimation);
                            break;
                        default:
                            backspaceBtn.startAnimation(blinkAnimation);
                            break;
                    }
                }

                if(getTextFromTextView(numberDialedTextView).equals("911")){
                    if(!checkWifiOnAndConnected()) { //we do not allow anyone to move forward unless wifi connection is enabled.
                        Toast.makeText(getApplicationContext(),"Please connect to a hotspot or wireless network.",Toast.LENGTH_LONG).show();
                        finish();
                        Intent homeActivity = new Intent(SimulatedDialpadActivity.this, HomeActivity.class);
                        startActivity(homeActivity);
                    } else {
                        finish();
                        Intent makeCall = new Intent(SimulatedDialpadActivity.this, MakeCallActivity.class);
                        // Send this information to the SimulatedCall and start that activity
                        makeCall.putExtra(ConnectActivity.CONTACT_NAME, contactName);
                        makeCall.putExtra(ConnectActivity.CONTACT_IP, contactIp);
                        makeCall.putExtra(ConnectActivity.CONTACT_DISPLAYNAME, displayName);
                        startActivity(makeCall);
                    }
                }
            }
        });

        backspaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //correct button to press next logic
                if(getTextFromTextView(numberDialedTextView) != null){
                    numberDialedTextView.setText(removeLastChar(getTextFromTextView(numberDialedTextView)));
                    clearAnimations();
                    switch (getTextFromTextView(numberDialedTextView)){
                        case "":
                            number9Btn.startAnimation(blinkAnimation);
                            break;
                        case "9":
                        case "91":
                            number1Btn.startAnimation(blinkAnimation);
                            break;
                        case "911":
                            callBtn.startAnimation(blinkAnimation);
                            break;
                        default:
                            backspaceBtn.startAnimation(blinkAnimation);
                            break;
                    }
                }
                else{
                    number9Btn.startAnimation(blinkAnimation);
                }
            }
        });
    }

    private static String getTextFromTextView(TextView tv){
        try{
            return tv.getText().toString();
        }
        catch(Exception e){
            return null;
        }
    }

    //takes in text view to grab string and append
    public static String appendDialed(String dialedNum, String pressedBtn){
        return dialedNum + pressedBtn;
    }

    public static String removeLastChar(String s){
        String slicedString;

        //attempt to create a substring and return it
        try{
            slicedString = s.substring(0, s.length() - 1);

            return slicedString;
        }catch (IndexOutOfBoundsException e)
        {//if we cant, return an empty string
            return "";
        }
    }

    //how do we want to teach??
    //let them make mistakes and backspace
    //or not let them make mistakes and show them correct button
    public static Boolean isWrongBtn(String curDialed, String btn){
        switch (curDialed){
            case "":
                return !btn.equals("9");
            case "9":
            case "91":
                return !btn.equals("1");
            case "911":
                return !btn.equals("call");
            default:
                return true;
        }
    }
    private boolean checkWifiOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if( wifiInfo.getNetworkId() == -1 ){
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        }
        else {
            return false; // Wi-Fi adapter is OFF
        }
    }

    private void clearAnimations(){
        number1Btn.clearAnimation();
        number2Btn.clearAnimation();
        number3Btn.clearAnimation();
        number4Btn.clearAnimation();
        number5Btn.clearAnimation();
        number6Btn.clearAnimation();
        number7Btn.clearAnimation();
        number8Btn.clearAnimation();
        number9Btn.clearAnimation();
        number0Btn.clearAnimation();
        asteriskBtn.clearAnimation();
        poundBtn.clearAnimation();
        callBtn.clearAnimation();
        backspaceBtn.clearAnimation();
    }


    @Override
    public void onBackPressed() {
        //do nothing.
    }
}
