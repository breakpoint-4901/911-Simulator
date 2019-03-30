package com.example.a911simulator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SimulatedHomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulated_home_screen);

        ImageButton phoneButton = findViewById(R.id.phoneIconButton);

        //set listener for correct application (phone app)
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialpad = new Intent(SimulatedHomeScreenActivity.this, SimulatedDialpadActivity.class);

                startActivity(dialpad);
            }
        });

        //TODO: the other apps and 'correction' functionality
    }
}
