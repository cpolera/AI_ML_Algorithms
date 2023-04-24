package Feedforward.LoggerTests;


import models.FeedForward.Logger;
import models.FeedForward.components.NNObj;
import models.FeedForward.components.Network;
import models.FeedForward.components.Node;
import models.FeedForward.components.OutputNode;
import com.google.gson.Gson;

import java.io.IOException;

import static models.FeedForward.FeedForwardExampleGenerator.createTrainingSymbolList;
import static models.FeedForward.FeedForwardExampleGenerator.createTestSymbolList;

public class LoggerMainTest {


    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();

        LogObject1 logObject = new LogObject1();
        String json = gson.toJson(logObject);

        System.out.println(json);

        NNObj[] trainingSet = createTrainingSymbolList();
        NNObj[] testSymbols = createTestSymbolList();
        Network network = new Network();
        Logger logger = new Logger(network);
        network.setupNetwork(3, 1, trainingSet, testSymbols);

        Node[] nodes = network.getHiddenLayerNodes(0);
        OutputNode[] outputNodes = network.getOutputLayerNodes();

        String nodeList = gson.toJson(nodes);
        String outputNodesList = gson.toJson(outputNodes);
        System.out.println(nodeList);
        System.out.println(outputNodesList);
        String networkString = gson.toJson(network);

        System.out.println(networkString);
    }



}
