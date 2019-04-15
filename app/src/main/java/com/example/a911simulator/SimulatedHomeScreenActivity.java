package com.example.a911simulator;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.OffsetDateTime;


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
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        //getWindow().setNavigationBarColor(Color.TRANSPARENT);
        //TODO: the other apps and 'correction' functionality

        TextView textView = findViewById(R.id.day_of_week);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            OffsetDateTime offset = OffsetDateTime.now();
            textView.setText(String.valueOf(offset.getDayOfWeek() + ", " + offset.getYear()));
        }

        TextView textViewMonth = findViewById(R.id.month);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            OffsetDateTime offset = OffsetDateTime.now();
            textViewMonth.setText(String.valueOf(offset.getMonth() + " " + offset.getDayOfMonth()));
        }
    }

    public void didTapButton(View view) {
        ImageView glow_button = findViewById(R.id.glow_button);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.glow);
        glow_button.startAnimation(myAnim);
    }

    @Override
    public void onBackPressed() {

        finish();
        Intent simulatedScenario = new Intent(SimulatedHomeScreenActivity.this, StudentScenarioActivity.class);
        // Send this information to the SimulatedDialpad and start that activity
        simulatedScenario.putExtra(ConnectActivity.CONTACT_NAME, contactName);
        simulatedScenario.putExtra(ConnectActivity.CONTACT_IP, contactIp);
        simulatedScenario.putExtra(ConnectActivity.CONTACT_DISPLAYNAME, displayName);
        startActivity(simulatedScenario);
    }
}