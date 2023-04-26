package com.implementations.models.FeedForward.components;

import com.implementations.models.FeedForward.Logger;

public class OutputNode extends Node {

    public double target;

    public OutputNode(double biasVal, double biasWeight, double target) {
        this.biasVal = biasVal;
        this.biasWeight = biasWeight;
        this.target = target;
    }

    /**
     * Calculates the error signal specifically for output node
     * where layer k = m
     * @return
     */
    @Override
    public double calculateError() {
        double output = getOutputVal();
        return (target - output) * output * (1 - output);
        // One source indicates this should be:
        // output * (1 - output) * (output - target) aka the negative of what I have now
    }

    @Override
    public void calculateNodeOutput() {
        super.calculateNodeOutput();
        Logger.log("OUTPUT NODE: " + getOutputVal(), 4);
    }

}
