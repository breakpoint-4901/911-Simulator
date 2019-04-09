package com.example.a911simulator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;


public class SimulatedHomeScreenActivity extends AppCompatActivity {

    private String displayName;
    private String contactName;
    private String contactIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulated_home_screen);

        //get data from previous intent.
        Intent intent = getIntent();
        displayName = intent.getStringExtra(ConnectActivity.CONTACT_DISPLAYNAME);
        contactName = intent.getStringExtra(ConnectActivity.CONTACT_NAME);
        contactIp = intent.getStringExtra(ConnectActivity.CONTACT_IP);

        //set listener for correct application (phone app)
        ImageButton phoneButton = findViewById(R.id.phoneIconButton);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent dialpad = new Intent(SimulatedHomeScreenActivity.this, SimulatedDialpadActivity.class);
                // Send this information to the SimulatedDialpad and start that activity
                dialpad.putExtra(ConnectActivity.CONTACT_NAME, contactName);
                dialpad.putExtra(ConnectActivity.CONTACT_IP, contactIp);
                dialpad.putExtra(ConnectActivity.CONTACT_DISPLAYNAME, displayName);
                startActivity(dialpad);
            }
        });

        //TODO: the other apps and 'correction' functionality

    }
    public void didTapButton(View view) {
        ImageButton glow_button = findViewById(R.id.glow_button);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.glow);
        glow_button.startAnimation(myAnim);
    }
}
