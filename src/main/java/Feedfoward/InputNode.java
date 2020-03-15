package Feedfoward;

public class InputNode extends Connection {

    public double inputValue;

    public InputNode() {
        super(null, null);
    }

    public InputNode(double inputValue) {
        super(null, null);
        this.inputValue = inputValue;
    }

}
