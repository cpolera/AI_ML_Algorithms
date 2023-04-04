package algorithms.FeedForward.components;

import algorithms.FeedForward.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static algorithms.FeedForward.NNMath.calculateTSSE;

public class Network {
    transient int trainingEpoch = 0; // TODO: logging value

    public transient double desiredError = 0.01;        // TODO: network config
    public transient double TSSE = 0.02;                // TODO: network config
    public transient double RMSE = 0.1;                 // TODO: network config
    public transient double acceptablePassRate = 0.95;  // TODO: network config
    public transient double learningRate = 0.5;// n     // TODO: network config

    public transient int trainingCount = 10000;         // TODO: training config
    public transient int maxTestCyclesPerTraining = 5;  // TODO: training config

    transient public double biasVal = 1;                // TODO: network config
    transient public int hiddenNeuronCount = 3;         // TODO: network config
    transient public int hiddenNeuronLayersCount = 1;   // TODO: network config
    transient private int totalCount_RESETBEFOREPREDICITION = 0;    // TODO: validation config
    transient private int passCount = 0;                            // TODO: validation config
    transient private int failCount = 0;                            // TODO: validation config
    private int passCountTotal = 0;                                 // TODO: validation config
    private int failCountTotal = 0;                                 // TODO: validation config
    private int testCountTotal = 0;                                 // TODO: validation config
    private int trainCountTotal = 0;                                // TODO: validation config

    private transient int minTrainingFactor = 1; // TODO: training config
    private transient double[] predictionValueActual;   // TODO: validation component
    private transient double[] predictionValueExpected; // TODO: validation component

    private transient ArrayList<Double[]> trainingDesired = new ArrayList<>();
    private transient ArrayList<Double[]> trainingActual = new ArrayList<>();
    private transient NNObj[] _trainingObjs;
    private transient NNObj[] _testObjs;

    private InputNode[] inputNodes; // TODO: network component
    Node[][] nodes;                 // TODO: network component

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

        setupNetwork(trainingObjs, testingObjs, run);
    }

    public void setupNetwork(NNObj[] trainingObjs, NNObj[] testingObjs, boolean run) throws IOException {
        _trainingObjs = trainingObjs;
        _testObjs = testingObjs;

        setupNetworkBase(hiddenNeuronLayersCount, hiddenNeuronCount, biasVal);
        Logger.logStaticVals();
        Logger.logInputs();

        if(run) {
            runNetworkTrainingAndTesting();
        }
    }

    public void runNetworkTrainingAndTesting() throws IOException {
        boolean endTraining = false;
        int testingCount = 0;
        while (!endTraining) { // Train/test until meets threshold
            passCount = 0;
            failCount = 0;
            trainNetwork();

            testNetwork(_testObjs); // TODO: test only needs to run once since it will be the same for a given test set
            testingCount++;
            passCountTotal += passCount;
            failCountTotal += failCount;

            // Determine if training should continue
            if (testingCount >= maxTestCyclesPerTraining || 1.0 * passCount / (passCount + failCount) > acceptablePassRate) {
                endTraining = true;
            }
        }
    }

    public void predictOutput(NNObj[] objs) {
        System.out.println("PREDICTING OUTCOME==========================================================");
        testNetwork(objs);
    }

    private void calculateNodeOutputs(Boolean save, Boolean test) {
//        for (int i = 0; i < nodes.length; i++) {
//            for (Node node : nodes[i]) {
//                node.calculateNodeOutput(false, true);
//            }
//        }

        for (int i = 0; i < hiddenNeuronLayersCount + 1; i++) {
            for (Node node : nodes[i]) {
                double tempVal = node.calculateNodeOutput(save, test);
                if (node instanceof OutputNeuron) {
                    System.out.print("OUTPUT NODE: " + tempVal);
                }
            }
        }
    }

    public void trainNetwork() throws IOException {
        // TODO: Investigate why this broke things
//        while(TSSE >= desiredError && totalCountTraining < trainingCount/minTrainingFactor){
        // Run the training sets x trainingCount
        for (int i = 0; i < trainingCount; i++) {
            // Run each training set
            for (NNObj nnObj : _trainingObjs) {
                if (TSSE <= desiredError && i > trainingCount / minTrainingFactor) {
                    //break; // TODO: clarify purpose
                }

                System.out.println("Training set: " + i);// TODO: LOGGER
                System.out.println(Arrays.toString(nnObj.getInputVals()) +
                        " ::: " + Arrays.toString(nnObj.getOutputVals())); // TODO: LOGGER
                setValuesInNetwork(nnObj);
                calculateNodeOutputs(true, false);

                updateErrorSignals();
                updateWeights();

                //updatePatternSum(); // TODO: Keep for now
                setRMSE();
                trainingEpoch++;
                trainCountTotal++;//LOGGER ____ USE AS COUNTER FOR FILE WRITE
                Logger.log();
            }
//            }
            // After running all training sets this time, show RMSE and TSSE
            System.out.println("RMSE: " + RMSE + " | TSSE: " + TSSE);
        }

        System.out.println("//*****************END TRAINING*******************//");
    }

    // TODO: Not clear when to use this. Appears to be important to updating TSSE
    // Called from trainNetwork, but commented out now
    private void updatePatternSum() {
        Double[] desired = new Double[nodes[nodes.length - 1].length];
        Double[] actual = new Double[nodes[nodes.length - 1].length];
        Node[] nodesOut = nodes[nodes.length - 1];

        for (int i = 0; i < nodes[nodes.length - 1].length; i++) {
            Node outputNode = nodesOut[i];
            if (outputNode instanceof OutputNeuron) {
                desired[i] = ((OutputNeuron) outputNode).target;
                actual[i] = outputNode.outputVal;
            }
        }
        trainingDesired.add(desired);
        trainingActual.add(actual);

        TSSE = calculateTSSE(trainingDesired, trainingActual);
    }

    /**
     * Method takes NNObj[] argument. If null, then use _testObjs
     * @param nnObjs
     */
    public void testNetwork(NNObj[] nnObjs) {
        totalCount_RESETBEFOREPREDICITION = 0;
        predictionValueActual = new double[nnObjs.length];
        predictionValueExpected = new double[nnObjs.length];

        // Test each test set
        for (NNObj testObj : nnObjs) {
            testNetworkHelper(testObj);
            //TODO THIS SHOULD BE A SEPARATE THING
//                Logger.log();
        }

        System.out.println("Pass: " + passCount + " | Fail: " + failCount); // TODO: these are per test cycle. shouldnt be here
        // or maybe this is for post validation?
        System.out.println("TestCount: " + testCountTotal);
        System.out.println("TrainingCount: " + trainCountTotal);
        System.out.println("TotalPass: " + passCountTotal + " | TotalFail: " + failCountTotal);
    }

    private void setValuesInNetwork(NNObj nnObj) {
        int count = 0;
        //for each input val: assign to input node - Same amount of nodes as input val
        for (double d : nnObj.getInputVals()) {
            this.inputNodes[count].inputValue = d;
            count++;
        }

        //TODO not efficient but works
        for (int j = 0; j < nodes[nodes.length - 1].length; j++) {
            Node node = nodes[nodes.length - 1][j];
            if (node instanceof OutputNeuron) {
                ((OutputNeuron) node).target = nnObj.getOutputVals()[j];
            }
        }
    }

    public void testNetworkHelper(NNObj nnObj) {
        this.testCountTotal++;
        setValuesInNetwork(nnObj);
        System.out.println("***NODE OUTPUT CALC***");

        calculateNodeOutputs(false, true);

        System.out.println(Arrays.toString(nnObj.getInputVals()));
        boolean tester = true;
        for (Node outputNeuron : nodes[nodes.length - 1]) {
            if (outputNeuron instanceof OutputNeuron) {
                System.out.println("Output:" + outputNeuron.tempOutput);
                predictionValueActual[totalCount_RESETBEFOREPREDICITION] = outputNeuron.tempOutput;
                predictionValueExpected[totalCount_RESETBEFOREPREDICITION] = ((OutputNeuron) outputNeuron).target;

                if (checkOutputAgainstTarget_TESTING((OutputNeuron) outputNeuron)) {
                    System.out.print("-----------------------------------PASSED");//TODO LOGGER
                    passCount++;
                } else {
                    tester = false;
                    System.out.print("-----------------------------------FAILED");//TODO LOGGER
                    failCount++;
                }
            }
            System.out.println("");
        }
        totalCount_RESETBEFOREPREDICITION++;

        if (!tester) {
            System.out.println("THIS TEST HAS FAILED ON ONE OR MORE OUTPUTS");
        } else {
            System.out.println("!!!!!!!!!!!!!!!!!PASSED TEST!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        System.out.println("");
    }

    private boolean checkOutputAgainstTarget_TESTING(OutputNeuron outputNeuron) {

        double outputTempVal = outputNeuron.tempOutput;

        if (outputTempVal < outputNeuron.target - desiredError || outputTempVal > outputNeuron.target + desiredError) {
            System.out.println("TARGET IS NOT WITHIN PERMISSIBLE RANGES. TERMINATING RUN.");
            return false;
        } else if (outputTempVal >= outputNeuron.target - desiredError && outputTempVal <= outputNeuron.target + desiredError) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Setup the network and nodes along with connections and base data
     *
     * @param hiddenNeuronLayers
     * @param hiddenNeuronCount
     * @param biasVal
     */
    private void setupNetworkBase(int hiddenNeuronLayers, int hiddenNeuronCount, double biasVal) {

        this.hiddenNeuronLayersCount = hiddenNeuronLayers;
        this.hiddenNeuronCount = hiddenNeuronCount;
        nodes = new Node[hiddenNeuronLayers + 1][];

        createInputLayer(_trainingObjs[0].getInputVals().length);
        createHiddenNeurons(biasVal);
        createOutputLayer(_trainingObjs[0].getOutputVals().length, biasVal);
        setupConnections();

        //setRMSE();
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

    private void setRMSE() {
        if (TSSE != 1) {
            RMSE = NNMath.calcRMSE(TSSE, _trainingObjs.length, _trainingObjs[0].getOutputVals().length);
        }
    }

    private void updateErrorSignals() {
        for (int nC = nodes.length - 1; nC >= 0; nC--) {
            for (int i = nodes[nC].length - 1; i >= 0; i--) {
                if (nodes[nC][i] instanceof OutputNeuron) {
                    nodes[nC][i].calculateError();
                } else {
                    nodes[nC][i].calculateError();
                }
                //System.out.println(nodes[nC][i].sigma);
            }
        }

    }

    private void updateWeights() {
        for (Node[] value : nodes) {
            for (Node node : value) {
                node.updateWeights(learningRate);
            }
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
