package models.FeedForward.components;

public class HiddenNode extends Node{

    public HiddenNode(double biasVal, double biasWeight) {
        super(biasVal, biasWeight);
    }

    public double calculateError() {
        double sigma = getOutputVal() * (1 - getOutputVal());
        double sumPostSynapticNeurons = calculatePostSynapticNeurons();
        return sigma * sumPostSynapticNeurons;
    }

    public double calculatePostSynapticNeurons() {
        //iterate through connections
        double subTotal = 0;
        for (Connection connection : outputConnections) {
            subTotal += connection.outputNeuron.calculateError() * connection.weight;
        }
        return subTotal;
    }
}
