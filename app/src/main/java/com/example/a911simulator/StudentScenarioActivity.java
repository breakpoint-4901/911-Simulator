package com.example.a911simulator;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.Locale;

public class StudentScenarioActivity extends AppCompatActivity {

    TextView scenarioText;
    Button studentScenarioButton, regenScenario;
    ImageButton ttsBtn;
    ScenarioGenerator scenarioGen;
    ScenarioGenerator.Scenario scenario;

    private String displayName;
    private String contactName;
    private String contactIp;

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_scenario);

        scenarioText = findViewById(R.id.scenarioText);
        studentScenarioButton = findViewById(R.id.studentScenarioButton);
        regenScenario = findViewById(R.id.regenScenario);
        ttsBtn = findViewById(R.id.textToSpeechBtn);

        //initiate text-to-speech and verify usage for current locale
        tts = new TextToSpeech(StudentScenarioActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result = tts.setLanguage(getResources().getConfiguration().locale);

                    //if tts cannot work, show on screen and hide tts button
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        ttsBtn.setVisibility(View.GONE);

                        Toast.makeText(getApplicationContext(), "Text to speech functionality will not work for the set language.", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    ttsBtn.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Text to speech functionality will not work for the set language.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //grab scenario
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
                //if text to speech is talking, stop
                if(tts.isSpeaking()){
                    tts.stop();
                }

                finish();
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
                //if text to speech is talking, stop
                if(tts.isSpeaking()){
                    tts.stop();
                }

                // regenerate a new scenario
                scenario = scenarioGen.getRandomScenario();
                scenarioText.scrollTo(0,0); //scroll back to the top
                scenarioText.setMovementMethod(new ScrollingMovementMethod());
                scenarioText.setText(scenario.getText());
            }
        });

        ttsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggle voice
                if(tts.isSpeaking()){
                    tts.stop();
                }
                else {
                    tts.speak(scenario.getText(), TextToSpeech.QUEUE_FLUSH, null, null);
                }
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

    @Override
    public void onBackPressed() {
        //do nothing.
    }
}
