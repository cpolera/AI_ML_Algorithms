package Feedfoward;

import java.util.ArrayList;

public class Node {

    double biasVal;
    double biasWeight;
    double net = 0;
    double outputVal;
    ArrayList<Connection> inputConnections = new ArrayList<>();
    ArrayList<Connection> outputConnections = new ArrayList<>();
    double sigma;
    double tempOutput = -1;

    public Node(double biasVal, double biasWeight) {
        this.biasVal = biasVal;
        this.biasWeight = biasWeight;
    }

    public Node() {
    }

    public void addInputConnection(Connection connection) {
        inputConnections.add(connection);
    }

    public void addOutputConnection(Connection connection) {
        outputConnections.add(connection);
    }

    private double calulateNet() {
        return calculateNet(true);
    }

    private double calculateNet(boolean save) {
        //net = bias*Wbias + SUM incoming outputs*their weight
        double calcBiasVal = this.biasWeight * biasVal;

        double tempVal;
        double sum = 0;

        //THIS IS SUM OF INCOMING INPUTS AKA PREVIOUS LAYER OUTPUTS
        for (Connection connection : inputConnections) {
            if (connection.inputNode != null) {
                sum += connection.getWeight() * connection.inputNode.inputValue;//Connection weight * input val if connection is to input
            } else {
                double outputVal = connection.getInputNeuron().outputVal;//Get outputVal of the hidden neuron
                if (connection.getInputNeuron().tempOutput > -1) {//TODO NOT SURE IF CORRECT
                    outputVal = connection.getInputNeuron().tempOutput;//IF TESTING, GET TEMP OUTPUT
                }
                sum += connection.getWeight() * outputVal; //Connection weight * output of the hidden neuron to next layer
            }
        }

        tempVal = calcBiasVal + sum;
        if (save)
            this.net = calcBiasVal + sum;

        return tempVal;
    }

    public double calculateNodeOutput(Boolean save, Boolean test) {
        double netVal = calculateNet(save);
        double tempVal = NNMath.sigmoidFunc(netVal);
        if (save)
            outputVal = tempVal;
        if (test) {
            tempOutput = tempVal;
        }
        return tempVal;
    }

    public double calculateNodeOutput(Boolean save) {//CALLED BY TRAINING
        double netVal = calculateNet(save);
        double tempVal = NNMath.sigmoidFunc(netVal);
        if (save)
            outputVal = tempVal;
        return tempVal;
    }

    public double calculateError() {
        sigma = outputVal * (1 - outputVal);
        double sumPostSynapticNeurons = calculatePostSynapticNeurons();
        return sigma *= sumPostSynapticNeurons;
    }

    public double calculatePostSynapticNeurons() {
        //iterate through connections
        double subTotal = 0;
        for (Connection connection : outputConnections) {
            subTotal += connection.outputNeuron.sigma * connection.weight;
        }
        return subTotal;
    }

    public void updateWeights(double learningRate) {
        biasWeight += learningRate * sigma * biasVal;//UPDATE OWN BIAS WEIGHT. CAN UPDATE BIAS HERE IF ENABLED
        //backpropogating the error from this node, to the bias ^^^

        for (Connection connection : inputConnections) {
            if (connection instanceof InputNode) {
                connection.weight += learningRate * sigma * ((InputNode) connection).inputValue;//see if this is ever used
            } else if (connection.inputNeuron != null) {
                connection.weight += learningRate * sigma * connection.inputNeuron.outputVal;
            } else if (connection.inputNode != null) {
                connection.weight += learningRate * sigma * connection.inputNode.inputValue;
            }
        }

    }
}
