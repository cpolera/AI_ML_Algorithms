package algorithms.Feedfoward;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Node {

    @Expose double biasVal;
    @Expose double biasWeight;
    double net = 0;
    @Expose
    public double outputVal;
    ArrayList<Connection> inputConnections = new ArrayList<>();
    ArrayList<Connection> outputConnections = new ArrayList<>();
    @Expose double sigma;
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

    private double calculateNet(boolean save) {
        //net = bias*Wbias + SUM incoming outputs*their weight
        double nodeBiasCalc = this.biasWeight * biasVal;

        double returnValue;
        double sum = 0;

        //THIS IS SUM OF INCOMING INPUTS AKA PREVIOUS LAYER OUTPUTS
        for (Connection connection : inputConnections) {//Go through each incoming connection
            if (connection.inputNode != null) {//Logic for Input nodes specifically
                sum += connection.getWeight() * connection.inputNode.inputValue;//Connection weight * input val if connection is to input
            } else {//Logic for hidden neuron inputs
                double outputVal = connection.getInputNeuron().outputVal;//Get outputVal of the hidden neuron
                if (connection.getInputNeuron().tempOutput > -1) {
                    outputVal = connection.getInputNeuron().tempOutput;//IF TESTING, GET TEMP OUTPUT INSTEAD//TODO Probably log when skipped
                }
                sum += connection.getWeight() * outputVal; //Connection weight * output of the hidden neuron to next layer
            }
        }

        returnValue = nodeBiasCalc + sum;
        if (save) {
            this.net = returnValue;//Update node net value. THIS IS NOT OUTPUT
        }

        return returnValue;
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
