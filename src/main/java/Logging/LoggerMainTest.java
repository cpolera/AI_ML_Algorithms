package Logging;


import Feedfoward.Main;
import Feedfoward.NNObj;
import Feedfoward.Network.Network;
import Feedfoward.Node;
import Feedfoward.OutputNeuron;
import com.google.gson.Gson;

import java.io.IOException;

public class LoggerMainTest {


    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();

        LogObject1 logObject = new LogObject1();
        String json = gson.toJson(logObject);

        System.out.println(json);

        NNObj[] trainingSet = Main.createSymbolList_EXAMPLE();
        NNObj[] testSymbols = Main.createTestSymbols();
        Network network = new Network();
        network.setupNetwork(trainingSet, testSymbols, false);

        Node[] nodes = network.getHiddenLayerNodes(0);
        OutputNeuron[] outputNeurons = (OutputNeuron[]) network.getHiddenLayerNodes(1);

        String nodeList = gson.toJson(nodes);
        String outputNodesList = gson.toJson(outputNeurons);
        System.out.println(nodeList);
        System.out.println(outputNodesList);
        String networkString = gson.toJson(network);

        System.out.println(networkString);
    }



}
