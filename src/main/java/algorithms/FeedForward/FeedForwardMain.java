package algorithms.FeedForward;

import algorithms.FeedForward.components.NNObj;
import algorithms.FeedForward.components.Network;

import java.io.IOException;

import static algorithms.FeedForward.FeedForwardHelper.createSymbolList_EXAMPLE;
import static algorithms.FeedForward.FeedForwardHelper.createTestSymbols;

public class FeedForwardMain {

    public static void main(String args[]) throws IOException {
        Network network = new Network();
        Logger logger = new Logger(network);
        NNObj[] trainingSet = createSymbolList_EXAMPLE();
        NNObj[] testSymbols = createTestSymbols();
        network.trainingCount = 100;
        network.desiredError = 0.1;
        network.setupNetwork(25, 2, trainingSet, testSymbols, true);

        logger.closeWriter();
        logger.showNetwork();

    }

//update to take any number of inputs
    //update to have any number of output neurons
    //update to handle multiple layers of hidden neurons
    //update training method to handle any number of inputs/ same with testing



}
