package algorithms.FeedForward.components;

import algorithms.FeedForward.*;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class Network {
    transient int trainingEpoch = 0; // TODO: logging value

    public transient double desiredError = 0.01;        // network config
    public transient double TSSE = 0.02;                // network config
    public transient double RMSE = 0.1;                 // network config
    public transient double acceptablePassRate = 0.95;  // network config
    public transient double learningRate = 0.5;// n     // network config
    transient public double biasVal = 1;                // network config
    transient public int hiddenNeuronCount;             // network config
    transient public int hiddenNeuronLayersCount;       // network config
    private transient NNObj[] _trainingObjs;
    private transient NNObj[] _testObjs;
    private InputNode[] inputNodes;                 // network component
    Node[][] nodes;                                 // network component

    /**
     * Runs all test sets X times per cycle
     * allows control number of trainings before running test cycle. Saves time by reducing test time.
     * Also useful if the dataset is smaller and you dont expect usable values with a single cycle
     */
    public transient int trainingCountPerCycle = 1;        // training config

    /**
     * Runs the training cycle X times
     * Every training cycle has a test phase which combined with trainingCountPerCycle allows control over
     * the total trainings while only testing as desired
     */
    public transient int maxTrainingCycles = 1;        // training config //

    private int passCountTotal = 0;
    private int failCountTotal = 0;
    int trainCountTotal = 0;                                // validation config

    public Network() {
    }

    // TODO: put together a builder to replace this
    public Network(NNObj[] trainingObjs, NNObj[] testObjs) throws IOException {
        this._trainingObjs = trainingObjs;
        this. _testObjs = testObjs;
        new Logger(this);
        setupNetwork(10, 1, trainingObjs, testObjs, true);
    }

    // TODO: put together a builder to partially replace this monstrosity
    // trainingObjs informs the input layer node count - not great to be implicit in this class
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

    public void runNetworkTrainingAndTesting() {
        Logger.log("Running network training and testing...", 1);
        NetworkTrainer networkTrainer = new NetworkTrainer(this);
        NetworkTester networkTester = new NetworkTester(this);
        boolean endTrainingEarly = false;
        int trainingCycleCount = 0;
        while ( trainingCycleCount < maxTrainingCycles && !endTrainingEarly ) {
            Logger.log("Executing training cycle: " + trainingCycleCount, 2);
            networkTrainer.trainNetwork(_trainingObjs, trainingCountPerCycle);
            Map<String, Integer> results = networkTester.testNetwork(_testObjs); // TODO: test only needs to run once since it will be the same for a given test set
            passCountTotal += results.get("passed");
            failCountTotal += results.get("failed");
            trainingCycleCount++;

            // Determine if training should continue
            if ( Integer.parseInt(System.getProperty("FFN_DEBUG_LEVEL")) == 1
                    && 1.0 * results.get("passed") / (results.get("passed") + results.get("failed")) > acceptablePassRate) {
                endTrainingEarly = true;
                Logger.log("Training ended early...", 2);
            }
        }
        Logger.log("Completed network training and testing.", 1);
        Logger.log("Training Count: " + trainCountTotal, 2);
        Logger.log("Test Count: " + (passCountTotal + failCountTotal) + " | Total Pass: " + passCountTotal + " | Total Fail: " + failCountTotal, 2);
    }

    void setValuesInNetwork(NNObj nnObj) {
        Logger.log("Setting network values...", 3);
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
        Logger.log("Finished setting network values.", 3);
    }

    public void calculateNodeOutputs(Boolean save, Boolean test) {
        Logger.log("Calculating node outputs...", 3);
        for (int i = 0; i < nodes.length; i++) {
            for (Node node : nodes[i]) {
                double tempVal = node.calculateNodeOutput(save, test);
                if (node instanceof OutputNeuron && !test) { // Test function has its own log for now
                    Logger.log("OUTPUT NODE: " + tempVal, 4);
                }
            }
        }
        Logger.log("Finished calculating node outputs.", 3);
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

    public Node[] getOutputLayerNodes(){
        return nodes[nodes.length - 1];
    }
}
