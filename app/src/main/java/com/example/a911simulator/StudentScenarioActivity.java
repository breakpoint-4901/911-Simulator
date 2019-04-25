package com.example.a911simulator;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.os.ConfigurationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

public class StudentScenarioActivity extends AppCompatActivity {

    private TextView scenarioText;
    private Button studentScenarioButton;
    private Button regenScenario;
    private ImageButton ttsBtn;
    private ScenarioGenerator scenarioGen;
    private ScenarioGenerator.Scenario scenario;

    private String displayName;
    private String contactName;
    private String contactIp;

    private TextToSpeech tts;
    private AudioManager audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_scenario);

        //grab ui elements
        scenarioText = findViewById(R.id.scenarioText);
        studentScenarioButton = findViewById(R.id.studentScenarioButton);
        regenScenario = findViewById(R.id.regenScenario);
        ttsBtn = findViewById(R.id.textToSpeechBtn);

        //give the regenerate scenario button a larger 'hit box'
        increaseHitAreaOfBy(regenScenario, 20);

        //initiate text-to-speech and verify usage for current locale
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    //FIXME: toggle comment for each one pls
                    int result = tts.setLanguage(ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0));
                    //int result = tts.setLanguage(ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0));

                    //if tts cannot work, show on screen and hide tts button
                    if(result == TextToSpeech.LANG_NOT_SUPPORTED){
                        ttsBtn.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Text to speech language is not supported", Toast.LENGTH_LONG).show();
                    }
                    else if(result == TextToSpeech.LANG_MISSING_DATA){
                        ttsBtn.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Text to speech missing data on language.", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    ttsBtn.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Text to speech functionality is not supported.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //set media volume to 50%
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float percent = 0.5f;
        int halfVolume = (int) (maxVolume * percent);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, halfVolume, 0);

        //grab scenario
        generateScenario();
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

    private void generateScenario() {

        AssetManager assetManager = getAssets(); //we store our files into the assets folder.
        String filename = getString(R.string.scenario_file_name); //scalability.

        InputStream in;
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

    //takes in a button and increases its 'hit box' for ui tapping
    private void increaseHitAreaOfBy(final Button btn, final int delta){
        final View parent = (View) btn.getParent();
        parent.post(new Runnable() {
            @Override
            public void run() {
                //create new rectangular hit box
                final Rect rect = new Rect();
                btn.getHitRect(rect);

                //increase bounds of shape
                rect.top -= delta;
                rect.left -= delta;
                rect.right += delta;
                rect.bottom += delta;

                //set button 'hit area' as new rect
                parent.setTouchDelegate( new TouchDelegate(rect, btn));
            }
        });
    }
}
