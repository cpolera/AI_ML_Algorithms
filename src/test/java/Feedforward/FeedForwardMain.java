package Feedforward;

import common.implementations.models.FeedForward.Logger;
import common.implementations.models.FeedForward.components.NNObj;
import common.implementations.models.FeedForward.components.Network;

import java.io.IOException;

import static common.implementations.models.FeedForward.FeedForwardExampleGenerator.createTrainingSymbolList;
import static common.implementations.models.FeedForward.FeedForwardExampleGenerator.createTestSymbolList;

import io.github.cdimascio.dotenv.Dotenv;

public class FeedForwardMain {

    public static void main(String[] args) throws IOException {
        Dotenv.configure().systemProperties().load();

        Network network = new Network();
        Logger logger = network.getLogger();
        NNObj[] trainingSet = createTrainingSymbolList();
        NNObj[] testSymbols = createTestSymbolList();
        network.trainingCountPerCycle = 1000;
        network.maxTrainingCycles = 1;
        network.desiredError = 0.1;
        network.setupNetwork(2, 1, trainingSet, testSymbols);
//        network.setupNetwork(25, 2, trainingSet, testSymbols);
        network.runNetworkTrainingAndTesting();

        logger.closeWriter();
        logger.showNetwork();
    }
}
