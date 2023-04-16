package models.FeedForward.components;

import models.FeedForward.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NetworkTester {
    Network network;
    private transient double[] predictionValueActual;
    private transient double[] predictionValueExpected;


    public NetworkTester(Network network){
        this.network = network;
    }

    /**
     * Method takes NNObj[] argument. If null, then use _testObjs
     *
     * @param nnObjs
     * @return
     */
    public Map<String, Integer> testNetwork(NNObj[] nnObjs) {
        Logger.log("Testing network...", 2);
//        int[] results = new int[]{0,0};
        Map<String, Integer> results  = new HashMap<String, Integer>() {{
            put("passed", 0);
            put("failed", 0);
        }};
        int predictionIndex = 0;
        predictionValueActual = new double[nnObjs.length];
        predictionValueExpected = new double[nnObjs.length];
        // Run each test set once
        for (NNObj testObj : nnObjs) {
            network.setValuesInNetwork(testObj);
            network.calculateNodeOutputs(false, true);
            boolean passed = validateOutput(testObj, predictionIndex);

            if(passed) {
                results.merge("passed", 1, Integer::sum);
            }
            else {
                results.merge("failed", 1, Integer::sum);
            }

            predictionIndex++;
        }
        Logger.log("Cycle Pass: " + results.get("passed") + " | Cycle Fail: " + results.get("failed"), 3);
        Logger.log("Finished testing network.", 2);
        return results;
    }

    public boolean validateOutput(NNObj nnObj, int predictionIndex) {
        Logger.log("Validating output...", 4);
        Logger.log("Input values: " + Arrays.toString(nnObj.getInputVals()), 5);
        int failedNeuronsCount = 0;
        for (Node outputNeuron : network.getOutputLayerNodes()) {
            if (outputNeuron instanceof OutputNode) {
                String logPrepend = "Output: " + outputNeuron.tempOutput;
                predictionValueActual[predictionIndex] = outputNeuron.tempOutput;
                predictionValueExpected[predictionIndex] = ((OutputNode) outputNeuron).target;

                if (checkOutputAgainstTarget((OutputNode) outputNeuron)) {
                    Logger.log(logPrepend + " -------------PASSED", 5);
                } else {
                    failedNeuronsCount++;
                    Logger.log(logPrepend + " -------------FAILED", 5);
                }
            }
            Logger.logNetworkState();
        }
        if (failedNeuronsCount > 0) {
            Logger.log("THIS TEST HAS FAILED ON " + failedNeuronsCount + " OUTPUT(S)", 4);
            return false;
        } else {
            Logger.log("PASSED TEST", 4);
            return true;
        }
    }

    // TODO: only used with testing, may need to see how training checks
    private boolean checkOutputAgainstTarget(OutputNode outputNode) {
        double outputTempVal = outputNode.tempOutput;

        if (outputTempVal < outputNode.target - network.desiredError || outputTempVal > outputNode.target + network.desiredError) {
            Logger.log("TARGET IS NOT WITHIN PERMISSIBLE RANGES. TERMINATING RUN.", 5);
            return false;
        } else return outputTempVal >= outputNode.target - network.desiredError && outputTempVal <= outputNode.target + network.desiredError;
    }
}
