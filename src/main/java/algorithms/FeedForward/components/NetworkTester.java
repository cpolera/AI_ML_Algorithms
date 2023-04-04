package algorithms.FeedForward.components;

import algorithms.FeedForward.Logger;

import java.util.Arrays;

public class NetworkTester {
    Network network;
    private transient double[] predictionValueActual;
    private transient double[] predictionValueExpected;


    public NetworkTester(Network network){
        this.network = network;
    }

    /**
     * Method takes NNObj[] argument. If null, then use _testObjs
     * @param nnObjs
     */
    public void testNetwork(NNObj[] nnObjs) {
        int predictionIndex = 0;
        predictionValueActual = new double[nnObjs.length];
        predictionValueExpected = new double[nnObjs.length];
        System.out.println("Beginning test of the network...");
        // Run each test set once
        for (NNObj testObj : nnObjs) {
            network.setValuesInNetwork(testObj);
            network.calculateNodeOutputs(false, true);
            validateOutput(testObj, predictionIndex);
//                Logger.log();
            System.out.println("Cycle Pass: " + network.cyclePassCount + " | Cycle Fail: " + network.cycleFailCount);
            predictionIndex++;
        }
        Logger.log("//*****************END TESTING*******************//", 1);
    }

    public void validateOutput(NNObj nnObj, int predictionIndex) {
        Logger.log("Input values: " + Arrays.toString(nnObj.getInputVals()), 5);
        Logger.log("Validating output...", 4);
        int failedNeuronsCount = 0;
        for (Node outputNeuron : network.getOutputLayerNodes()) {
            if (outputNeuron instanceof OutputNeuron) {
                String logPrepend = "Output: " + outputNeuron.tempOutput;
                predictionValueActual[predictionIndex] = outputNeuron.tempOutput;
                predictionValueExpected[predictionIndex] = ((OutputNeuron) outputNeuron).target;

                if (checkOutputAgainstTarget((OutputNeuron) outputNeuron)) {
                    Logger.log(logPrepend + " -------------PASSED", 5);
                } else {
                    failedNeuronsCount++;
                    Logger.log(logPrepend + " -------------FAILED", 5);
                }
            }
            Logger.logNetworkState();
        }

        if (failedNeuronsCount > 0) {
            network.cycleFailCount++;
            Logger.log("THIS TEST HAS FAILED ON " + failedNeuronsCount + "OUTPUT(S)", 4);
        } else {
            network.cyclePassCount++;
            Logger.log("!!!!!!!!!!!!!!!!!PASSED TEST!!!!!!!!!!!!!!!!!!!!!!!!", 4);
        }
        Logger.logNetworkState();
    }

    // TODO: only used with testing, may need to see how training checks
    private boolean checkOutputAgainstTarget(OutputNeuron outputNeuron) {
        double outputTempVal = outputNeuron.tempOutput;

        if (outputTempVal < outputNeuron.target - network.desiredError || outputTempVal > outputNeuron.target + network.desiredError) {
            Logger.log("TARGET IS NOT WITHIN PERMISSIBLE RANGES. TERMINATING RUN.", 5);
            return false;
        } else return outputTempVal >= outputNeuron.target - network.desiredError && outputTempVal <= outputNeuron.target + network.desiredError;
    }
}
