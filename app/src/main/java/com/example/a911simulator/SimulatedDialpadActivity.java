package com.example.a911simulator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class SimulatedDialpadActivity extends AppCompatActivity {

    private String displayName;
    private String contactName;
    private String contactIp;

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
        final ImageButton number1Btn = findViewById(R.id.oneImageBtn);
        ImageButton number2Btn = findViewById(R.id.twoImageBtn);
        ImageButton number3Btn = findViewById(R.id.threeImageBtn);
        ImageButton number4Btn = findViewById(R.id.fourImageBtn);
        ImageButton number5Btn = findViewById(R.id.fiveImageBtn);
        ImageButton number6Btn = findViewById(R.id.sixImageBtn);
        ImageButton number7Btn = findViewById(R.id.sevenImageBtn);
        ImageButton number8Btn = findViewById(R.id.eightImageBtn);
        final ImageButton number9Btn = findViewById(R.id.nineImageBtn);
        ImageButton number0Btn = findViewById(R.id.zeroImageBtn);
        ImageButton asteriskBtn = findViewById(R.id.asteriskImageBtn);
        ImageButton poundBtn = findViewById(R.id.poundImageBtn);
        final ImageButton callBtn = findViewById(R.id.callImageBtn);
        final ImageButton backspaceBtn = findViewById(R.id.backspaceBtn);
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
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "1"));

            }
        });

        number2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "2")){
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "2"));
            }
        });

        number3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "3")){
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "3"));
            }
        });

        number4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "4")){
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "4"));
            }
        });

        number5Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "5")){
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "5"));
            }
        });

        number6Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "6")){
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "6"));
            }
        });

        number7Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "7")){
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "7"));
            }
        });

        number8Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "8")){
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "8"));
            }
        });

        number9Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "9")){
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "9"));
            }
        });

        number0Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "0")){
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "0"));
            }
        });

        asteriskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "*")){
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "*"));
            }
        });

        poundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "#")){
                    backspaceBtn.startAnimation(blinkAnimation);
                }

                numberDialedTextView.setText(appendDialed(getTextFromTextView(numberDialedTextView), "#"));
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWrongBtn(getTextFromTextView(numberDialedTextView), "call")){
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
                    finish();
                    Intent makeCall = new Intent(SimulatedDialpadActivity.this, MakeCallActivity.class);
                    // Send this information to the SimulatedCall and start that activity
                    makeCall.putExtra(ConnectActivity.CONTACT_NAME, contactName);
                    makeCall.putExtra(ConnectActivity.CONTACT_IP, contactIp);
                    makeCall.putExtra(ConnectActivity.CONTACT_DISPLAYNAME, displayName);
                    startActivity(makeCall);
                }
            }
        });

        backspaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //correct button to press next logic
                if(getTextFromTextView(numberDialedTextView) != null){
                    numberDialedTextView.setText(removeLastChar(getTextFromTextView(numberDialedTextView)));

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

    @Override
    public void onBackPressed() {
        //do nothing.
    }
}
