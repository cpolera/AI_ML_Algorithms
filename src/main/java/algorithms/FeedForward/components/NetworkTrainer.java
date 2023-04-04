package algorithms.FeedForward.components;

import algorithms.FeedForward.Logger;
import algorithms.FeedForward.NNMath;

import java.util.ArrayList;
import java.util.Arrays;

import static algorithms.FeedForward.NNMath.calculateTSSE;

public class NetworkTrainer {

    Network network;
    int trainingCount;
    private transient int minTrainingFactor = 1;
    private transient ArrayList<Double[]> trainingDesired = new ArrayList<>();
    private transient ArrayList<Double[]> trainingActual = new ArrayList<>();

    public NetworkTrainer(Network network, int trainingCount){
        this.network = network;
        this.trainingCount = trainingCount;
    }

    public void trainNetwork(NNObj[] trainingObjs) {
        // TODO: Investigate why this broke things
//        while(network.TSSE >= network.desiredError && network.trainCountTotal < trainingCount/minTrainingFactor){ // TODO: this truncates mid cycle. Should we ever do this?
            // Run the training sets x trainingCount
            for (int i = 0; i < trainingCount; i++) {
                Logger.log("Training cycle: " + i, 4); // TODO: LOGGER
                // Run each training set
                for (NNObj nnObj : trainingObjs) {
                    Logger.log("Input: " + Arrays.toString(nnObj.getInputVals()), 5); // TODO: LOGGER
                    Logger.log("Expected Outputs: " + Arrays.toString(nnObj.getOutputVals()), 5); // TODO: LOGGER
                    network.setValuesInNetwork(nnObj);
                    network.calculateNodeOutputs(true, false);

                    updateErrorSignals();
                    updateWeights();

                    //updatePatternSum(); // TODO: Keep for now
                    setRMSE(trainingObjs);
                    network.trainingEpoch++;
                    network.trainCountTotal++;//LOGGER ____ USE AS COUNTER FOR FILE WRITE
                    Logger.log();
                }
            }
            // After running all training sets this time, show RMSE and TSSE
            Logger.log("RMSE: " + network.RMSE + " | TSSE: " + network.TSSE, 4);
//        }

        Logger.log("//*****************END TRAINING*******************//", 1);
    }

    private void updateErrorSignals() {
        for (int nC = network.nodes.length - 1; nC >= 0; nC--) {
            for (int i = network.nodes[nC].length - 1; i >= 0; i--) {
                network.nodes[nC][i].calculateError();
                //Logger.log(nodes[nC][i].sigma, 5);
            }
        }
    }

    private void updateWeights() {
        for (Node[] value : network.nodes) {
            for (Node node : value) {
                node.updateWeights(network.learningRate);
            }
        }
    }

    private void setRMSE(NNObj[] trainingObjs) {
        if (network.TSSE != 1) {
            network.RMSE = NNMath.calcRMSE(network.TSSE, trainingObjs.length, trainingObjs[0].getOutputVals().length);
        }
    }

    // TODO: Not clear when to use this. Appears to be important to updating TSSE
    // Called from trainNetwork, but commented out now
    private void updatePatternSum() {
        Node[][] nodes = network.nodes;
        Double[] desired = new Double[nodes[nodes.length - 1].length];
        Double[] actual = new Double[nodes[nodes.length - 1].length];
        Node[] nodesOut = nodes[nodes.length - 1];

        for (int i = 0; i < nodes[nodes.length - 1].length; i++) {
            Node outputNode = nodesOut[i];
            if (outputNode instanceof OutputNeuron) {
                desired[i] = ((OutputNeuron) outputNode).target;
                actual[i] = outputNode.outputVal;
            }
        }
        trainingDesired.add(desired);
        trainingActual.add(actual);

        network.TSSE = calculateTSSE(trainingDesired, trainingActual);
    }
}
