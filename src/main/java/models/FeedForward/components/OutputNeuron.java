package models.FeedForward.components;

public class OutputNeuron extends Node {

    public double target;

    public OutputNeuron(double biasVal, double biasWeight, double target) {
        this.biasVal = biasVal;
        this.biasWeight = biasWeight;
        this.target = target;
    }

    @Override
    public double calculateError() {
        double output = getOutputVal();
        sigma = (target - output) * (output * (1 - output));
        return sigma;
    }

}
