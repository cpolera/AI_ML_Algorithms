package Feedfoward.Network;

import Feedfoward.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Network {
    transient int epoch = 0;

    public transient double desiredError = 0.01;
    public transient double TSSE = 0.02;
    public transient double RMSE = 0.1;
    public transient double acceptablePassRate = 0.95;
    public transient double learningRate = 0.5;//n
    public transient int trainingCount = 10000;
    public transient int howManyToTest = 5;//TODO is this needed?
    public transient int testCount = 20;

    transient public double biasVal = 1;
    transient public int hiddenNeuronCount = 3;
    transient public int hiddenNeuronLayersCount = 1;
    transient private int totalCount_RESETBEFOREPREDICITION = 0;
    transient private int passCount = 0;
    transient private int failCount = 0;
    private int passCountTotal = 0;
    private int failCountTotal = 0;
    private int testCountTotal = 0;
    private int trainCountTotal = 0;

    private transient int minTrainingFactor = 1;
    private transient double[] predictionValueActual;
    private transient double[] predictionValueExpected;
    private int totalCountTraining = 0;

    private transient ArrayList<Double[]> trainingDesired = new ArrayList<>();
    private transient ArrayList<Double[]> trainingActual = new ArrayList<>();
    private transient NNObj[] _trainingObjs;
    private transient NNObj[] _testObjs;
    private InputNode[] inputNodes;

    Node[][] nodes;

    private transient Random random = new Random();


    public Node[] getHiddenLayerNodes(int i){return nodes[i];}

    public Network() {
    }

    public Network(NNObj[] trainingObjs, NNObj[] testObjs) throws IOException {
        this._trainingObjs = trainingObjs;
        this. _testObjs = testObjs;
        new Logger(this);
        setupNetwork(10, 1, trainingObjs, testObjs, true);
    }

    public void setupNetwork(NNObj[] trainingObjs, NNObj[] testingObj, boolean run) throws IOException {

        _trainingObjs = trainingObjs;
        _testObjs = testingObj;

        setupNetworkBase(hiddenNeuronLayersCount, hiddenNeuronCount, biasVal);
        //use list of inputs/expected outputs for running training/testing
        Logger.logStaticVals();
        Logger.logInputs();

        if(run) {runNetworkTrainingAndTesting();}
    }

    public void runNetworkTrainingAndTesting() throws IOException {
        boolean endTesting = false;
        int testingCount = 0;
        while (!endTesting) {//Train/test until meets threshold
            passCount = 0;
            failCount = 0;
            trainNetwork(null);

            testNetwork(null);
            if (testingCount >= howManyToTest || 1.0 * passCount / (passCount + failCount) > acceptablePassRate) {
                endTesting = true;
            }
            testingCount++;
            passCountTotal += passCount;
            failCountTotal += failCount;
        }
    }

    public void predictOutput(NNObj[] objs) {

        System.out.println("PREDICTING OUTCOME==========================================================");
        int temp = this.testCount;
        this.testCount = 1;
        testNetwork(objs);
        this.testCount = temp;
    }

    public void setupNetwork(int hiddenNeuronCount, int neuronLayers, NNObj[] trainingObjs, NNObj[] testingObjs, boolean run) throws IOException {
        this.hiddenNeuronCount = hiddenNeuronCount;
        this.hiddenNeuronLayersCount = neuronLayers;

        setupNetwork(trainingObjs, testingObjs, run);
    }

    private void setRMSE() {
        if (TSSE != 1) {
            RMSE = NNMath.calcRMSE(TSSE, _trainingObjs.length, _trainingObjs[0].getOutputVals().length);
        }
    }

    private void calculateNodeOutputs() {
        for (int i = 0; i < hiddenNeuronLayersCount + 1; i++) {
            for (Node node : nodes[i]) {
                if (node instanceof OutputNeuron) {
                    System.out.print("OUTPUT NODE: ");
                    System.out.println(node.calculateNodeOutput(true));
                } else {
                    node.calculateNodeOutput(true);
                }
            }
        }
    }

    //NETWORK ALREADY KNOWS WHAT TRAINING OBJS ARE. DONT NEED TO PASS THEM IN
    /**
     * Method to train the Network. Can pass in an Integer. If null, then use the currently set trainingCount
     * @param trainingCountManual
     */
    public void trainNetwork(Integer trainingCountManual) throws IOException {

        int trainingCountUse = trainingCountManual == null ? trainingCount : trainingCountManual;
        //XXX dunno why this broke things
//        while(TSSE >= desiredError && totalCountTraining < trainingCount/minTrainingFactor){
        for (int i = 0; i < trainingCountUse; i++) {//Run training sets this many times

            for (NNObj nnObj : _trainingObjs) {//Run each training set
                if (TSSE <= desiredError && i > trainingCountUse / minTrainingFactor) {
                    //break; //TODO y tho?
                }

                System.out.println("Training set: " + i);//LOGGER
                System.out.println(Arrays.toString(nnObj.getInputVals()) + " ::: " + Arrays.toString(nnObj.getOutputVals()));//LOGGER
                setValuesInNetwork(nnObj);
                calculateNodeOutputs();

                updateErrorSignals();
                updateWeights();

                //updateTSSE();
                setRMSE();
                epoch++;
                trainCountTotal++;//LOGGER ____ USE AS COUNTER FOR FILE WRITE
                Logger.log();
            }
//            }
            System.out.println("RMSE: " + RMSE + " | TSSE: " + TSSE);
        }


        System.out.println("//*****************END TRAINING*******************//");
    }

    public void updateTSSE() {
        updatePatternSum();
    }

    //WHAT DOES THIS DOOOOOOOOO? //THINK THIS IS ACTUALLY IMPORTANT ONCE I IMPLEMENT UPDATING TSSE
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

        totalCountTraining++;

        TSSE = calculateTSSE(trainingDesired, trainingActual);
    }


    /**
     * Method takes NNObj[] argument. If null, then use _testObjs
     * @param nnObjs
     */
    public void testNetwork(NNObj[] nnObjs) {
        NNObj[] objsToUse = nnObjs == null ? _testObjs : nnObjs;
        //STORE INPUT NODE VALS
        double[] initialVals = new double[inputNodes.length];
        for (int i = 0; i < initialVals.length; i++) {
            initialVals[i] = inputNodes[i].inputValue;
        }

        for (int i = 0; i < testCount; i++) {
            totalCount_RESETBEFOREPREDICITION = 0;
            predictionValueActual = new double[objsToUse.length];
            predictionValueExpected = new double[objsToUse.length];

            for (NNObj testObj : objsToUse) {
                //System.out.println(setValues.nnObj.desc);
                testNetworkHelper(testObj);
                //TODO THIS SHOULD BE A SEPARATE THING
//                Logger.log();

            }
        }

        //REVERT INPUT NODE VALUES
        for (int i = 0; i < initialVals.length; i++) {
            inputNodes[i].inputValue = initialVals[i];
        }

        //resetNodeTempOutputs();

        System.out.println("Pass: " + passCount + " | Fail: " + failCount);
        System.out.println("TestCount: " + testCountTotal);
        System.out.println("TrainingCount: " + trainCountTotal);
        System.out.println("TotalPass: " + passCountTotal + " | TotalFail: " + failCountTotal);
    }

    //TODO change to two double[] input and output instead of setValues
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
        for (int count2 = 0; count2 < nodes.length; count2++) {
            for (Node node : nodes[count2]) {
                node.calculateNodeOutput(false, true);
            }
        }
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
            outputNeurons[count] = new OutputNeuron(biasVal, randomDouble(), 0.9);//TODO why is target 0.9?????????????????????????????????!!!!!!!
            count++;
        }

        nodes[nodes.length - 1] = outputNeurons;
    }

    //TODO ONLY WORKS FOR ONE HIDDEN LAYER
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

    private double calculateTSSE(ArrayList<Double[]> patternsE, ArrayList<Double[]> outActuals) {
        return 0.5 * calculateSumOfSquares(patternsE, outActuals);
    }

    private double calculateSumOfSquares(ArrayList<Double[]> patternsE, ArrayList<Double[]> outActuals) {
        double sum = 0.0;
        for (int i = 0; i < totalCountTraining; i++) {
            sum = sum + calculateSquaredError_OnePattern(patternsE.get(i), outActuals.get(i));
        }

        return sum;
    }

    private double calculateSquaredError_OnePattern(Double[] patExpect, Double[] outActual) {
        double sum = 0.0;
        for (int i = 0; i < patExpect.length; i++) {
            double temp = patExpect[i] - outActual[i];
            temp = temp * temp;
            sum = sum + temp;
        }
        return sum;
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

    public int getEpoch(){return epoch;}

    public Node[][] getNodes(){return nodes;}
}
