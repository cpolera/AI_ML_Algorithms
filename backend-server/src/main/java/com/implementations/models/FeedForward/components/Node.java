package com.implementations.models.FeedForward.components;

import com.implementations.models.FeedForward.NNMath;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {

    @Expose double biasVal;
    @Expose double biasWeight;
    @Expose private double outputVal;
    ArrayList<Connection> inputConnections = new ArrayList<>();
    ArrayList<Connection> outputConnections = new ArrayList<>();
    public HashMap<Integer, Connection> inputConnectionsMap = new HashMap<>();
    public HashMap<Integer, Connection> outputConnectionsMap = new HashMap<>();
    public double tempOutput = -1;
    private static int idCounter = 0;
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

    private double calculateNet() {
        //net = bias*Wbias + SUM incoming outputs*their weight
        double nodeBiasCalc = this.biasWeight * biasVal;

        return nodeBiasCalc + sumIncomingInputs(inputConnections);
    }

    private double sumIncomingInputs(ArrayList<Connection> connections){
        double sum = 0;

        //THIS IS SUM OF INCOMING INPUTS (PREVIOUS LAYER OUTPUTS)
        for (Connection connection : connections) { // Go through each incoming connection
            double outputVal = connection.getInputNeuron().outputVal; // Get outputVal of the hidden neuron
            sum += connection.getWeight() * outputVal; // Connection weight * output of the hidden neuron to next layer
        }
        return sum;
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

        // Update node bias weight ex: delta W H2, Bias
        double deltaBias = learningRate * error * biasVal;
        biasWeight = biasWeight + deltaBias;

        // Update incoming connection weights ex: delta W O3, H1 = 0.01685
        for (Connection connection : inputConnections) {
           if (connection.inputNeuron != null) {
               double deltaConnection = learningRate * error * connection.inputNeuron.getOutputVal();
               connection.weight += deltaConnection;
            }
        }
    }

    public double getOutputVal() {
        return outputVal;
    }

    public void setOutputVal(double outputVal) {
        this.outputVal = outputVal;
    }

    public double getBiasWeight() { return this.biasWeight; }

    public void setBiasWeight(double biasWeight) { this.biasWeight = biasWeight; }

    public int getId() { return this.id; }
}
