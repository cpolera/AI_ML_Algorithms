package models.FeedForward.components;

import models.FeedForward.NNMath;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Node {

    @Expose double biasVal;
    @Expose double biasWeight;
    @Expose
    private double outputVal;
    ArrayList<Connection> inputConnections = new ArrayList<>();
    ArrayList<Connection> outputConnections = new ArrayList<>();
    public double tempOutput = -1;
    private transient static int idCounter = 0;
    @Expose private int id;

    public Node(double biasVal, double biasWeight) {
        this.biasVal = biasVal;
        this.biasWeight = biasWeight;
        this.id=idCounter;
        idCounter++;
    }

    public Node() {
        this.id=idCounter;
        idCounter++;
    }

    public void addInputConnection(Connection connection) {
        inputConnections.add(connection);
    }

    public void addOutputConnection(Connection connection) {
        outputConnections.add(connection);
    }

    private double calculateNet() {
        //net = bias*Wbias + SUM incoming outputs*their weight
        double nodeBiasCalc = this.biasWeight * biasVal;
        double sum = 0;

        //THIS IS SUM OF INCOMING INPUTS (PREVIOUS LAYER OUTPUTS)
        for (Connection connection : inputConnections) { // Go through each incoming connection
            double outputVal = connection.getInputNeuron().outputVal; // Get outputVal of the hidden neuron
            sum += connection.getWeight() * outputVal; // Connection weight * output of the hidden neuron to next layer
        }

        return nodeBiasCalc + sum;
    }

    // Used by Hidden and Output Nodes
    public void calculateNodeOutput() {
        double netVal = calculateNet();
        setOutputVal(NNMath.sigmoidFunc(netVal));
    }

    // Temp until I go back to make interface
    // Output and Hidden nodes implement their own version of this
    public double calculateError() {
        return 0.0;
    }

    public void updateWeights(double learningRate) {
        double error = calculateError();
        biasWeight += learningRate * error * biasVal;//UPDATE OWN BIAS WEIGHT. CAN UPDATE BIAS HERE IF ENABLED
        //backpropagation of the error from this node, to the bias ^^^

        for (Connection connection : inputConnections) {
           if (connection.inputNeuron != null) {
                connection.weight += learningRate * error * connection.inputNeuron.outputVal;
            }
        }
    }

    public double getOutputVal() {
        return outputVal;
    }

    public void setOutputVal(double outputVal) {
        this.outputVal = outputVal;
    }
}
