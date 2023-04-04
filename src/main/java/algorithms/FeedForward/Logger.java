package algorithms.FeedForward;

import algorithms.FeedForward.components.Network;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    private static Network NETWORK;
    private static JsonWriter writer;

    static {
        try {
            writer = new JsonWriter( new FileWriter("src/logs/Log1.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Gson gson = new Gson();

    public Logger() throws IOException {
    }

    public Logger(Network network) throws IOException {

        this.NETWORK = network;
        writer.beginArray();
        writer.beginArray();

    }

    public static void logInputs() throws IOException {
        writer.beginArray();
        try{
            writer.beginObject();
            String trainingObjsS = gson.toJson(NETWORK.getTrainingObjs());
            writer.name("trainingObjs").value(trainingObjsS);
            writer.endObject();

            writer.beginObject();
            String testingObjsS = gson.toJson(NETWORK.getTestingObjs());
            writer.name("trainingObjs").value(testingObjsS);
            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.endArray();
    }

    public static void logStaticVals() throws IOException {
        writer.beginArray();
        try{
            writer.beginObject();
            writer.name("desiredError").value(NETWORK.desiredError);
            writer.name("TSSE").value(NETWORK.TSSE);
            writer.name("RMSE").value(NETWORK.RMSE);
            writer.name("acceptablePassRate").value(NETWORK.acceptablePassRate);
            writer.name("learningRate").value(NETWORK.learningRate);
            writer.name("trainingCount").value(NETWORK.trainingCount);
            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.endArray();
    }

    public static void log(){
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

    public void showNetowrkTrainingObjs(){
        System.out.println(gson.toJson(NETWORK.getTrainingObjs()));
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