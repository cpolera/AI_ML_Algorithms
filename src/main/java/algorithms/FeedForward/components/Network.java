package algorithms.FeedForward.components;

import algorithms.FeedForward.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static algorithms.FeedForward.NNMath.calculateTSSE;

public class Network {
    transient int trainingEpoch = 0; // TODO: logging value

    public transient double desiredError = 0.01;        // TODO: network config
    public transient double TSSE = 0.02;                // TODO: network config
    public transient double RMSE = 0.1;                 // TODO: network config
    public transient double acceptablePassRate = 0.95;  // TODO: network config
    public transient double learningRate = 0.5;// n     // TODO: network config
    transient public double biasVal = 1;                // TODO: network config
    transient public int hiddenNeuronCount;             // TODO: network config
    transient public int hiddenNeuronLayersCount;       // TODO: network config
    private transient NNObj[] _trainingObjs;
    private transient NNObj[] _testObjs;
    private InputNode[] inputNodes;                 // TODO: network component
    Node[][] nodes;                                 // TODO: network component

    public transient int trainingCount = 10000;        // TODO: training config
    public transient int maxTrainingCycles = 5;        // TODO: training config

    private int passCountTotal = 0;
    private int failCountTotal = 0;
    transient public int cyclePassCount = 0;                       // TODO: validation config
    transient public int cycleFailCount = 0;                       // TODO: validation config

    int trainCountTotal = 0;                                // TODO: validation config


    public Network() {
    }

    public Network(NNObj[] trainingObjs, NNObj[] testObjs) throws IOException {
        this._trainingObjs = trainingObjs;
        this. _testObjs = testObjs;
        new Logger(this);
        setupNetwork(10, 1, trainingObjs, testObjs, true);
    }

    public void setupNetwork(int hiddenNeuronCount, int neuronLayers, NNObj[] trainingObjs, NNObj[] testingObjs, boolean run) throws IOException {
        this.hiddenNeuronCount = hiddenNeuronCount;
        this.hiddenNeuronLayersCount = neuronLayers;
        _trainingObjs = trainingObjs;
        _testObjs = testingObjs;
        nodes = new Node[hiddenNeuronLayersCount + 1][]; // makes hidden layers plus output layer

        createInputLayer(_trainingObjs[0].getInputVals().length);
        createHiddenNeurons(biasVal);
        createOutputLayer(_trainingObjs[0].getOutputVals().length, biasVal);
        setupConnections();

        //setRMSE();
        Logger.logStaticVals();
        Logger.logInputs();

        if(run) {
            runNetworkTrainingAndTesting();
        }
    }

    public void runNetworkTrainingAndTesting() throws IOException {
        NetworkTrainer networkTrainer = new NetworkTrainer(this, trainingCount);
        NetworkTester networkTester = new NetworkTester(this);
        boolean endTraining = false;
        int trainingCycleCount = 0;
        while (!endTraining) { // Train/test until meets threshold
            cyclePassCount = 0;
            cycleFailCount = 0;
            networkTrainer.trainNetwork(_trainingObjs);
            networkTester.testNetwork(_testObjs); // TODO: test only needs to run once since it will be the same for a given test set
            passCountTotal += cyclePassCount;
            failCountTotal += cycleFailCount;
            trainingCycleCount++;

            // Determine if training should continue
            if (trainingCycleCount >= maxTrainingCycles || 1.0 * cyclePassCount / (cyclePassCount + cycleFailCount) > acceptablePassRate) {
                endTraining = true;
            }
        }
        System.out.println("TrainingCount: " + trainCountTotal);
        System.out.println("TestCount: " + (passCountTotal + failCountTotal));
        System.out.println("TotalPass: " + passCountTotal + " | TotalFail: " + failCountTotal);
        System.out.println("//*****************END TESTING*******************//");
    }





    void setValuesInNetwork(NNObj nnObj) {
        int count = 0;
        //for each input val: assign to input node - Same amount of nodes as input val
        for (double d : nnObj.getInputVals()) {
            this.inputNodes[count].inputValue = d;
            count++;
        }

        // TODO: not efficient but works
        for (int j = 0; j < nodes[nodes.length - 1].length; j++) {
            Node node = nodes[nodes.length - 1][j];
            if (node instanceof OutputNeuron) {
                ((OutputNeuron) node).target = nnObj.getOutputVals()[j];
            }
        }
    }

    public void calculateNodeOutputs(Boolean save, Boolean test) {
        System.out.println("***NODE OUTPUT CALC***");
        for (int i = 0; i < hiddenNeuronLayersCount + 1; i++) { // hidden layer count plus output layer
            for (Node node : nodes[i]) {
                double tempVal = node.calculateNodeOutput(save, test);
                if (node instanceof OutputNeuron) {
                    System.out.print("OUTPUT NODE: " + tempVal);
                }
            }
        }
    }

    /**
     * Assumes that biasVal is a static value for all nodes initially
     * @param biasVal
     */
    private void createHiddenNeurons(double biasVal) {
        for (int nC = 0; nC < hiddenNeuronLayersCount; nC++) {
            Node[] tempNodes = new Node[hiddenNeuronCount];
            for (int i = 0; i < hiddenNeuronCount; i++) {
                tempNodes[i] = new Node(biasVal, randomDouble());
            }
            nodes[nC] = tempNodes;
        }
    }

    private void createInputLayer(int count) {
        inputNodes = new InputNode[count];
        int c = 0;
        while (c < count) {
            inputNodes[c] = new InputNode();
            c++;
        }
    }

    private void createOutputLayer(int size, double biasVal) {
        int count = 0;
        OutputNeuron[] outputNeurons = new OutputNeuron[size];
        while (count < size) {
            outputNeurons[count] = new OutputNeuron(biasVal, randomDouble(), 0.9);// TODO: confirm static target here is ok
            count++;
        }

        nodes[nodes.length - 1] = outputNeurons;
    }

    //TODO: Only works for one hidden layer
    private void setupConnections() {
        int count = 0;
        for (InputNode inputNode : inputNodes) {
            for (Node node : nodes[0]) {
                new Connection(inputNode, node, randomDouble() + 0.1);
                if (count < 1) {
                    for (Node outputNeuron : nodes[nodes.length - 1]) {
                        new Connection(node, outputNeuron, randomDouble() + 0.1);
                    }
                }

            }
            count++;
        }
    }

    private double randomDouble() {
        Random random = new Random();
        double d = random.nextDouble();
//        d = d * 0.99 *100;
//        int temp = (int) d;
//        d = temp/100.0;

        if (random.nextInt(2) != 1) {
            d = d * -1;
        }
        return d;
    }

    public NNObj[] getTrainingObjs(){
        return _trainingObjs;
    }

    public NNObj[] getTestingObjs(){
        return _testObjs;
    }

    public int getTrainingEpoch(){
        return trainingEpoch;
    }

    public Node[][] getNodes(){
        return nodes;
    }

    public Node[] getHiddenLayerNodes(int i){
        return nodes[i];
    }
}
