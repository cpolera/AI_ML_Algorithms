package com.implementations.models.FeedForward.components;

import com.implementations.models.FeedForward.Logger;

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

    private InputNode[] inputLayer;                 // network component
    private HiddenNode[][] hiddenLayers;
    private OutputNode[] outputLayer;

    private transient Logger logger;

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

    public Network() throws IOException {
        this.logger = new Logger(this);
    }

    // TODO: put together a builder to replace this
    public Network(NNObj[] trainingObjs, NNObj[] testObjs) throws IOException {
        this._trainingObjs = trainingObjs;
        this. _testObjs = testObjs;
        this.logger = new Logger(this);
        setupNetwork(2, 1, trainingObjs, testObjs);
    }

    // TODO: put together a builder to partially replace this monstrosity
    // trainingObjs informs the input layer node count - not great to be implicit in this class
    public void setupNetwork(int hiddenNeuronCount, int neuronLayers, NNObj[] trainingObjs, NNObj[] testingObjs) throws IOException {
        this.hiddenNeuronCount = hiddenNeuronCount;
        this.hiddenNeuronLayersCount = neuronLayers;
        _trainingObjs = trainingObjs;
        _testObjs = testingObjs;

        createInputLayer(_trainingObjs[0].getInputVals().length);
        createHiddenLayers(neuronLayers, hiddenNeuronCount);
        createOutputLayer(_trainingObjs[0].getOutputVals().length);
        setupConnections();

        //setRMSE();
        logger.logStaticVals();
        logger.logInputs();
    }

    /**
     * Method to use the trained network on a given object.
     * Returns an array of outputs based on how the network was trained
     *
     * @param nnObj
     * @return
     */
    public double[] processInput(NNObj nnObj) {
        setValuesInNetwork(nnObj);
        calculateNodeOutputs();
        return getNetworkOutputs();
    }

    public double[] getNetworkOutputs(){
        double[] outputs = new double[outputLayer.length];
        for(int i=0; i<outputLayer.length; i++){
            outputs[i] = outputLayer[i].getOutputVal();
        }
        return outputs;
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
        // Set input values
        double[] inputValues = nnObj.getInputVals();
        for (int i = 0; i < inputLayer.length; i++) {
            inputLayer[i].setOutputVal(inputValues[i]);
        }

        // Set outputNode targets
        for (int j = 0; j < outputLayer.length; j++) {
            outputLayer[j].target = nnObj.getOutputVals()[j];
        }

        Logger.log("Finished setting network values.", 3);
    }

    public void calculateNodeOutputs() {
        Logger.log("Calculating node outputs...", 3);

        for (HiddenNode[] hiddenLayer : hiddenLayers) {
            for (HiddenNode hiddenNode : hiddenLayer) {
                hiddenNode.calculateNodeOutput();
            }
        }

        for (OutputNode outputNode: outputLayer) {
            outputNode.calculateNodeOutput();
        }

        Logger.log("Finished calculating node outputs.", 3);
    }

    /**
     * Assumes that biasVal is a static value for all nodes initially
     */
    private void createHiddenLayers(int layerCount, int neuronCountPerLayer) {
        hiddenLayers = new HiddenNode[layerCount][];

        for (int layer = 0; layer < hiddenLayers.length; layer++) {
            HiddenNode[] _nodes = new HiddenNode[neuronCountPerLayer];
            for (int i = 0; i < neuronCountPerLayer; i++) {
                _nodes[i] = new HiddenNode(biasVal, randomWeight()); // Set a random weight to start
            }
            hiddenLayers[layer] = _nodes;
        }
    }

    private void createInputLayer(int size) {
        inputLayer = new InputNode[size];
        int count = 0;
        while (count < size) {
            inputLayer[count] = new InputNode();
            count++;
        }
    }

    private void createOutputLayer(int size) {
        outputLayer = new OutputNode[size];
        int count = 0;
        while (count < size) {
            outputLayer[count] = new OutputNode(biasVal, randomWeight(), 0.9); // TODO: move default to OutputNode class and refactor a bit
            count++;
        }
    }

    // TODO: this is not using the node connections
    // Might be better to just replace this and the node connections objects with a single map. This is excessive
    // A node can know all its connections and a connection can know its nodes. This is problematic
    private void setupConnections() {
        Node[][] layers = getNodes();
        for (int layerIndex = 0; layerIndex < layers.length - 1; layerIndex++) {
            for (Node node: layers[layerIndex]) {
                for (Node upstreamNode: layers[layerIndex+1]){
                    new Connection(node, upstreamNode, randomWeight() + 0.1);
                }
            }
        }
    }

    private double randomWeight() {
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
        Node[][] nodes = new Node[2+hiddenLayers.length][];
        nodes[0] = inputLayer;
        nodes[nodes.length-1] = outputLayer;
        for(int i = 0; i < hiddenLayers.length; i++){
            nodes[i+1] = hiddenLayers[i];
        }
        return nodes;
    }

    public InputNode[] getInputLayerNodes(){
        return inputLayer;
    }

    public HiddenNode[] getHiddenLayerNodes(int i){
        return hiddenLayers[i];
    }

    public OutputNode[] getOutputLayerNodes(){
        return outputLayer;
    }

    public Logger getLogger(){
        return logger;
    }
}
