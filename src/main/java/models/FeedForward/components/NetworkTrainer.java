package models.FeedForward.components;

import models.FeedForward.Logger;
import models.FeedForward.NNMath;

import java.util.ArrayList;
import java.util.Arrays;

import static models.FeedForward.NNMath.calculateTSSE;

public class NetworkTrainer {

    Network network;
    private transient ArrayList<Double[]> trainingDesired = new ArrayList<>();
    private transient ArrayList<Double[]> trainingActual = new ArrayList<>();

    public NetworkTrainer(Network network){
        this.network = network;
    }
    public void trainNetwork(NNObj[] trainingObjs, int trainingCountPerCycle){
        Logger.log("Running training sets " + trainingCountPerCycle + " time(s).", 1);
        for(int i=0; i < trainingCountPerCycle; i++){
            trainNetwork(trainingObjs);
        }
    }

    public void trainNetwork(NNObj[] trainingObjs) {
        Logger.log("Training network...", 2);
        for (NNObj nnObj : trainingObjs) {
            Logger.log("Input: " + Arrays.toString(nnObj.getInputVals()), 5);
            Logger.log("Expected Outputs: " + Arrays.toString(nnObj.getOutputVals()), 5);
            network.setValuesInNetwork(nnObj);
            network.calculateNodeOutputs(true, false);

            Logger.log("Updating error signals and weights...", 5);
            updateErrorSignals();
            updateWeights();

            //updatePatternSum(); // TODO: Keep for now
            setRMSE(trainingObjs);
            network.trainingEpoch++;
            network.trainCountTotal++;//LOGGER ____ USE AS COUNTER FOR FILE WRITE
            Logger.logNetworkState();
        }

        // After running all training sets this time, show RMSE and TSSE
        Logger.log("RMSE: " + network.RMSE + " | TSSE: " + network.TSSE, 3);

        Logger.log("Finished training network.", 2);
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
            if (outputNode instanceof OutputNode) {
                desired[i] = ((OutputNode) outputNode).target;
                actual[i] = outputNode.getOutputVal();
            }
        }
        trainingDesired.add(desired);
        trainingActual.add(actual);

        network.TSSE = calculateTSSE(trainingDesired, trainingActual);
    }
}
