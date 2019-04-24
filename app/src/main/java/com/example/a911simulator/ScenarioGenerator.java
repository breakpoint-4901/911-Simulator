package com.example.a911simulator;

import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class ScenarioGenerator {

    private final ArrayList<Scenario> scenarios;
    private final Random randIndex;
    private int prevIndex;
    private final InputStream scenarioFile;

    public class Scenario {

        private final String text; //the line pulled from the .txt file

        Scenario(String text){
            this.text = text; //constructor
        }
        public String getText() {
            return text;
        }
    }

    public ScenarioGenerator(InputStream file){
        this.scenarioFile = file; //input stream (saved for future calls to the file. //never closed?
        scenarios = new ArrayList<>();
        readScenarioFile();
        randIndex = new Random();
        prevIndex = -1;
    }

    public Scenario getRandomScenario(){
        int index = randIndex.nextInt(scenarios.size()); //potential value
        //we allow for duplicates for optimization reasons.
        while(index == prevIndex){
            index = randIndex.nextInt(scenarios.size());
        }
        Scenario newScenario = scenarios.get(index);
        prevIndex = index;
        return newScenario;
    }

    private void readScenarioFile(){
        Scanner in;
        try {
            in = new Scanner(scenarioFile);
            while(in.hasNextLine()){ //each scenario is separated by a newline.
                scenarios.add(new Scenario(in.nextLine())); //fill up our datastructure with each new line.
            }
        }catch(Exception e){
            Log.i("Scenario_GEN", e.toString());
        }
    }

}