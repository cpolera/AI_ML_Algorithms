package algorithms.Feedfoward;

public class OutputNeuron extends Node {

    public double target;

    public OutputNeuron(double biasVal, double biasWeight, double target) {
        this.biasVal = biasVal;
        this.biasWeight = biasWeight;
        this.target = target;
    }

    @Override
    public double calculateError() {
        sigma = (target - outputVal) * (outputVal * (1 - outputVal));
        return sigma;
    }

}
