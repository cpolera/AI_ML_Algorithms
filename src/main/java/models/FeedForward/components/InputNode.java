package models.FeedForward.components;

public class InputNode extends Connection {

    private double inputValue;

    public InputNode() {
        super(null, null);
    }

    public void setInputValue(double inputValue) {
        this.inputValue = inputValue;
    }

    public double getInputValue() {
        return inputValue;
    }
}
