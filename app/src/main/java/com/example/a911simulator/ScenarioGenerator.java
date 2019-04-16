package com.example.a911simulator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class ScenarioGenerator {

    public ArrayList<Scenario> scenarios;
    public Random randIndex;
    public int prevIndex;
    public InputStream scenarioFile;

    public class Scenario {

        private String text; //the line pulled from the .txt file

        public Scenario(String text){
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

    public void readScenarioFile(){
        Scanner in;
        try {
            in = new Scanner(scenarioFile);
            while(in.hasNextLine()){ //each scenario is separated by a newline.
                scenarios.add(new Scenario(in.nextLine())); //fill up our datastructure with each new line.
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

}