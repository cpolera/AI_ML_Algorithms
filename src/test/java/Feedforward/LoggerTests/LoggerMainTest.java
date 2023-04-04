package Feedforward.LoggerTests;


import algorithms.FeedForward.*;
import algorithms.FeedForward.components.NNObj;
import algorithms.FeedForward.components.Network;
import algorithms.FeedForward.components.Node;
import algorithms.FeedForward.components.OutputNeuron;
import com.google.gson.Gson;

import java.io.IOException;

import static algorithms.FeedForward.FeedForwardHelper.createSymbolList_EXAMPLE;
import static algorithms.FeedForward.FeedForwardHelper.createTestSymbols;

public class LoggerMainTest {


    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();

        LogObject1 logObject = new LogObject1();
        String json = gson.toJson(logObject);

        System.out.println(json);

        NNObj[] trainingSet = createSymbolList_EXAMPLE();
        NNObj[] testSymbols = createTestSymbols();
        Network network = new Network();
        Logger logger = new Logger(network);
        network.setupNetwork(3, 1, trainingSet, testSymbols, false);

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
