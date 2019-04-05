package com.example.a911simulator;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.InetAddress;

public class StudentScenarioActivity extends AppCompatActivity {

    TextView scenarioText;
    Button studentScenarioButton, regenScenario;
    ScenarioGenerator scenarioGen;
    ScenarioGenerator.Scenario scenario;

    private String displayName;
    private String contactName;
    private String contactIp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_scenario);

        scenarioText = findViewById(R.id.scenarioText);
        studentScenarioButton = findViewById(R.id.studentScenarioButton);
        regenScenario = findViewById(R.id.regenScenario);
        generateScenerio();

        scenario = scenarioGen.getRandomScenario();

        scenarioText.setMovementMethod(new ScrollingMovementMethod());
        scenarioText.setText(scenario.getText());

        //get data from previous intent.
        Intent intent = getIntent();
        displayName = intent.getStringExtra(ConnectActivity.CONTACT_DISPLAYNAME);
        contactName = intent.getStringExtra(ConnectActivity.CONTACT_NAME);
        contactIp = intent.getStringExtra(ConnectActivity.CONTACT_IP);

        //sets listener for start of simulation
        studentScenarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent simulatedHomeScreen = new Intent(StudentScenarioActivity.this, SimulatedHomeScreenActivity.class);
                // Send this information to the SimulatedHomeScreen and start that activity
                simulatedHomeScreen.putExtra(ConnectActivity.CONTACT_NAME, contactName);
                simulatedHomeScreen.putExtra(ConnectActivity.CONTACT_IP, contactIp);
                simulatedHomeScreen.putExtra(ConnectActivity.CONTACT_DISPLAYNAME, displayName);
                startActivity(simulatedHomeScreen);
            }
        });
        regenScenario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // regenerate a new scenario
                scenario = scenarioGen.getRandomScenario();
                scenarioText.scrollTo(0,0); //scroll back to the top
                scenarioText.setMovementMethod(new ScrollingMovementMethod());
                scenarioText.setText(scenario.getText());
            }
        });
    }

    private void generateScenerio() {

        AssetManager assetManager = getAssets(); //we store our files into the assets folder.
        String filename = getString(R.string.scenario_file_name); //scalability.

        InputStream in = null;
        try {
            //attempt to open the file.
            in = assetManager.open(filename);
            scenarioGen = new ScenarioGenerator(in);

        } catch(java.io.IOException ex){
            Toast.makeText(StudentScenarioActivity.this, "This file does not exist.", Toast.LENGTH_SHORT).show();
        }
    }

}
