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

        // Run each test set once
        for (NNObj testObj : nnObjs) {
            network.setValuesInNetwork(testObj);
            network.calculateNodeOutputs(false, true);
            validateOutput(testObj, predictionIndex);
//                Logger.log();
            System.out.println("Cycle Pass: " + network.cyclePassCount + " | Cycle Fail: " + network.cycleFailCount);
            predictionIndex++;
        }
    }

    public void validateOutput(NNObj nnObj, int predictionIndex) {
        Node[][] nodes = network.getNodes();
        System.out.println(Arrays.toString(nnObj.getInputVals()));
        boolean tester = true;
        for (Node outputNeuron : nodes[nodes.length - 1]) {
            if (outputNeuron instanceof OutputNeuron) {
                System.out.println("Output:" + outputNeuron.tempOutput);
                predictionValueActual[predictionIndex] = outputNeuron.tempOutput;
                predictionValueExpected[predictionIndex] = ((OutputNeuron) outputNeuron).target;

                if (checkOutputAgainstTarget((OutputNeuron) outputNeuron)) {
                    System.out.print("-----------------------------------PASSED");//TODO LOGGER
                    network.cyclePassCount++;
                } else {
                    tester = false;
                    System.out.print("-----------------------------------FAILED");//TODO LOGGER
                    network.cycleFailCount++;
                }
            }
            System.out.println();
        }

        if (!tester) {
            System.out.println("THIS TEST HAS FAILED ON ONE OR MORE OUTPUTS");
        } else {
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
