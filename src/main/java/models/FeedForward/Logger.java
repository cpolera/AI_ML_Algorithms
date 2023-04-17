package models.FeedForward;

import models.FeedForward.components.Network;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;

/**
 * This class was initially built to capture the state of the network as it changed.
 * The output file is meant to be ingested by a different project that visualizes the network over time in 3D
 *
 * It is now also being updated to act as a proper logger for the network itself
 */
public class Logger {

    private Network NETWORK;
    private JsonWriter writer;

    private Gson gson = new Gson();

    public Logger() throws IOException {
    }

    public Logger(Network network) throws IOException {
        NETWORK = network;
        try {
            writer = new JsonWriter( new FileWriter("src/logs/Log1.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.beginArray();
        writer.beginArray();
    }

    public void logInputs() throws IOException {
        writer.beginArray();
        try{
            writer.beginObject();
            String trainingObjsS = gson.toJson(NETWORK.getTrainingObjs());
            writer.name("trainingObjs").value(trainingObjsS);
            writer.endObject();

            writer.beginObject();
            String testingObjsS = gson.toJson(NETWORK.getTestingObjs());
            writer.name("testingObjs").value(testingObjsS);
            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.endArray();
    }

    public void logStaticVals() throws IOException {
        writer.beginArray();
        try{
            writer.beginObject();
            writer.name("desiredError").value(NETWORK.desiredError);
            writer.name("TSSE").value(NETWORK.TSSE);
            writer.name("RMSE").value(NETWORK.RMSE);
            writer.name("acceptablePassRate").value(NETWORK.acceptablePassRate);
            writer.name("learningRate").value(NETWORK.learningRate);
            writer.name("trainingCountPerCycle").value(NETWORK.trainingCountPerCycle);
            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.endArray();
    }

    public void logNetworkState(){
        try {
            writer.beginObject();
            String networkS = gson.toJson(NETWORK);
            writer.name(""+NETWORK.getTrainingEpoch()).value(networkS);
            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showNetwork(){
        System.out.println(gson.toJson(NETWORK));
    }

    public void showNetworkNodes(){
        System.out.println(gson.toJson(NETWORK.getNodes()));
    }

    public void showNetworkTrainingObjs(){
        System.out.println(gson.toJson(NETWORK.getTrainingObjs()));
    }

    public static void log(String logString, int debugLevel){
        if(debugLevel <= Integer.parseInt(System.getProperty("FFN_DEBUG_LEVEL"))){
            System.out.println(logString);
        }
    }

    public void closeWriter(){
        try {

            writer.endArray();
            writer.endArray();

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*

    2. Update training/testing objs to use an ID and store reference instead. Store the objs in seperate file or in its own layer
    3. add option to round doubles to a certain number of decimal places

 */