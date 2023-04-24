package common.implementations.models.FeedForward.components;

public class Connection {

    double weight;
    transient Node inputNeuron;
    transient Node outputNeuron;
    private static int nextId = 1;
    protected int id;

    public Connection(Node input, Node output) {
        this.inputNeuron = input;
        this.outputNeuron = output;
        this.id= nextId;
        nextId++;
    }

    public Connection(Node input, Node output, double weight) {
        this.inputNeuron = input;
        input.outputConnections.add(this);
        input.outputConnectionsMap.put(output.getId(), this);
        this.outputNeuron = output;
        output.inputConnections.add(this);
        output.inputConnectionsMap.put(input.getId(), this);
        this.weight = weight;
        this.id= nextId;
        nextId++;
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
