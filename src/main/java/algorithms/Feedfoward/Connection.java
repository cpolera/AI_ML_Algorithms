package algorithms.Feedfoward;

public class Connection {

    double weight;
    transient Node inputNeuron;
    transient Node outputNeuron;
    transient InputNode inputNode;//NOT SAME AS A HIDDEN NODE
    private static int idCounter = 0;
    private int id;

    public Connection(Node input, Node output) {
        this.inputNeuron = input;
        this.outputNeuron = output;
        this.id=idCounter;
        idCounter++;
    }

    public Connection(Node input, Node output, double weight) {
        this.inputNeuron = input;
        input.outputConnections.add(this);
        this.outputNeuron = output;
        output.inputConnections.add(this);
        this.weight = weight;
        this.id=idCounter;
        idCounter++;
    }

    public Connection(InputNode inputNode, Node output, double weight) {
        this.inputNode = inputNode;
        this.outputNeuron = output;
        output.inputConnections.add(this);
        this.weight = weight;
        this.id=idCounter;
        idCounter++;
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
