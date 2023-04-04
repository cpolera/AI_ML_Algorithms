package algorithms.FeedForward;

import algorithms.FeedForward.components.NNObj;
import algorithms.FeedForward.components.Network;

import java.io.IOException;

import static algorithms.FeedForward.FeedForwardHelper.createSymbolList_EXAMPLE;
import static algorithms.FeedForward.FeedForwardHelper.createTestSymbols;

import io.github.cdimascio.dotenv.Dotenv;

public class FeedForwardMain {

    public static void main(String args[]) throws IOException {
        Dotenv.configure().systemProperties().load();
        String tester = System.getProperty("FFN_TEST");
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

    // update to handle multiple layers of hidden neurons - TODO: need to validate still
    // update training method to handle any number of inputs/ same with testing
}
