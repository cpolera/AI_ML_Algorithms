package models.FeedForward.components;

public class InputNode {

    private double outputValue;
    private static int nextId = 1;
    protected int id;

    public InputNode() {}

    public void setOutputValue(double outputValue) {
        this.outputValue = outputValue;
    }

    public double getOutputValue() {
        return outputValue;
    }
}
