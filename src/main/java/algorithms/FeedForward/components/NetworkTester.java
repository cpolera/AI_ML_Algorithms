package algorithms.FeedForward.components;

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
        System.out.println("//*****************END TESTING*******************//");
    }

    public void validateOutput(NNObj nnObj, int predictionIndex) {
        System.out.println("Input values: " + Arrays.toString(nnObj.getInputVals()));
        System.out.println("Validating output...");
        int failedNeuronsCount = 0;
        for (Node outputNeuron : network.getOutputLayerNodes()) {
            if (outputNeuron instanceof OutputNeuron) {
                System.out.print("Output: " + outputNeuron.tempOutput); // TODO: Logger
                predictionValueActual[predictionIndex] = outputNeuron.tempOutput;
                predictionValueExpected[predictionIndex] = ((OutputNeuron) outputNeuron).target;

                if (checkOutputAgainstTarget((OutputNeuron) outputNeuron)) {
                    System.out.println(" -----------------------------------PASSED"); // TODO: LOGGER
                } else {
                    failedNeuronsCount++;
                    System.out.println(" -----------------------------------FAILED"); // TODO: LOGGER
                }
            }
            System.out.println();
        }

        if (failedNeuronsCount > 0) {
            network.cycleFailCount++;
            System.out.println("THIS TEST HAS FAILED ON " + failedNeuronsCount + "OUTPUT(S)");
        } else {
            network.cyclePassCount++;
            System.out.println("!!!!!!!!!!!!!!!!!PASSED TEST!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        System.out.println();
    }

    // TODO: only used with testing, may need to see how training checks
    private boolean checkOutputAgainstTarget(OutputNeuron outputNeuron) {
        double outputTempVal = outputNeuron.tempOutput;

        if (outputTempVal < outputNeuron.target - network.desiredError || outputTempVal > outputNeuron.target + network.desiredError) {
            System.out.println("TARGET IS NOT WITHIN PERMISSIBLE RANGES. TERMINATING RUN.");
            return false;
        } else return outputTempVal >= outputNeuron.target - network.desiredError && outputTempVal <= outputNeuron.target + network.desiredError;
    }
}
