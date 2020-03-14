package Feedfoward;

public class Connection {

    double weight;
    Node inputNeuron;
    Node outputNeuron;
    InputNode inputNode;//NOT SAME AS A HIDDEN NODE

    public Connection(Node input, Node output) {
        this.inputNeuron = input;
        this.outputNeuron = output;
    }

    public Connection(Node input, Node output, double weight) {
        this.inputNeuron = input;
        input.outputConnections.add(this);
        this.outputNeuron = output;
        output.inputConnections.add(this);
        this.weight = weight;
    }

    public Connection(InputNode inputNode, Node output, double weight) {
        this.inputNode = inputNode;
        this.outputNeuron = output;
        output.inputConnections.add(this);
        this.weight = weight;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double newWeight) {
        this.weight = newWeight;
    }

    public Node getInputNeuron() {
        return inputNeuron;
    }

    public Node getOutputNeuron() {
        return outputNeuron;
    }
}
