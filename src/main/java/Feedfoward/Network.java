package Feedfoward;

import FinalProjStocks.Stock;

import java.util.ArrayList;
import java.util.Random;

public class Network {
    //assumptions
    public double desiredError = 0.01;
    public double TSSE = 0.02;
    public double acceptablePassRate = 0.9;
    public double learningRate = 0.5;//n
    public int trainingCount = 10000;
    public double biasVal = 1;
    public int hiddenNeuronCount = 3;
    public int hiddenNeuronLayersCount = 1;
    public int howManyToTest = 5;
    public int totalCount_RESETBEFOREPREDICITION = 0;
    double RMSE = 0.1;
    Node[][] nodes;
    int epoch = 1;
    int patternCount;
    InputNode[] inputNodes;
    int testCount = 20;
    int passCount = 0;
    int failCount = 0;
    int passCountTotal = 0;
    int failCountTotal = 0;
    int testCountTotal = 0;
    int trainCountTotal = 0;
    Random random = new Random();
    NNObj[] nnObjs;
    NNObj[] testObjs;
    SetValues[] setValuesList;
    int minTrainingFactor = 1;
    int totalPatternCount = 0;
    int totalOutputCount = 0;
    double[] predictionValueActual;
    double[] predictionValueExpected;
    String[] predictionDate;
    int totalCountTraining = 0;
    private ArrayList<Double[]> trainingDesired = new ArrayList<>();
    private ArrayList<Double[]> trainingActual = new ArrayList<>();

    public Network() {
    }

    public Network(NNObj[] nnObjs) {
        this.nnObjs = nnObjs;
    }

    public Network(NNObj[] nnObjs, NNObj[] testObjs) {
        this.nnObjs = nnObjs;
        this.testObjs = testObjs;
    }

    public void setupNetwork(NNObj[] trainingObjs, NNObj[] testingObj) {
        this.setValuesList = updateSetValuesResults(trainingObjs);

        setupNetworkBase(hiddenNeuronLayersCount, hiddenNeuronCount, biasVal, trainingObjs[0].dVals.length, trainingObjs[0].dVals_targets.length);
        //use list of inputs/expected outputs for running training/testing
        boolean endTesting = false;
        int testingCount = 0;
        while (endTesting == false) {
            passCount = 0;
            failCount = 0;
            setValuesList = updateSetValuesResults(trainingObjs);
            trainNetwork(setValuesList);

            setValuesList = updateSetValuesResults(testingObj);

            testNetwork(setValuesList);
            if (testingCount >= howManyToTest || 1.0 * passCount / (passCount + failCount) > acceptablePassRate) {
                endTesting = true;
            }
            testingCount++;
            passCountTotal += passCount;
            failCountTotal += failCount;
        }

    }

    public void predictOutput(NNObj[] objs) {

        testObjs = objs;
        System.out.println("PREDICTING OUTCOME==========================================================");
        this.setValuesList = updateSetValuesResults(objs);
        int temp = this.testCount;
        this.testCount = 1;
        testNetwork(setValuesList);
        this.testCount = temp;
    }

    public void setupNetwork(int hiddenNeuronCount, int neuronLayers, NNObj[] trainingObjs, NNObj[] testingObjs) {
        this.hiddenNeuronCount = hiddenNeuronCount;
        this.hiddenNeuronLayersCount = neuronLayers;

        setupNetwork(trainingObjs, testingObjs);
    }

    private void setRMSE() {
        if (TSSE != 1) {
            RMSE = NNMath.calcRMSE(TSSE, setValuesList.length, setValuesList[0].outputVals.length);
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

    public void trainNetwork(SetValues[] setValuesList) {
        patternCount = setValuesList.length;

//        while(TSSE >= desiredError && totalCountTraining < trainingCount/minTrainingFactor){
        for (int i = 0; i < trainingCount; i++) {

            for (SetValues setValues : setValuesList) {
                if (TSSE <= desiredError && i > trainingCount / minTrainingFactor) {
                    //break;
                }

                System.out.println("Training set: " + i);
                System.out.println(setValues);
                setValuesInNetwork(setValues);
                trainCountTotal++;
                calculateNodeOutputs();

                updateErrorSignals();

                updateWeights();

                //updateTSSE();
                setRMSE();
            }
//            }
            System.out.println("RMSE: " + RMSE + " | TSSE: " + TSSE);
        }


        System.out.println("//*****************END TRAINING*******************//");

    }

    public void updateTSSE() {
        updatePatternSum();
    }

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

    public void testNetwork(SetValues[] setValuesList) {
        //STORE INPUT NODE VALS
        double[] initialVals = new double[inputNodes.length];
        for (int i = 0; i < initialVals.length; i++) {
            initialVals[i] = inputNodes[i].inputValue;
        }

        for (int i = 0; i < testCount; i++) {
            totalCount_RESETBEFOREPREDICITION = 0;
            predictionValueActual = new double[setValuesList.length];
            predictionValueExpected = new double[setValuesList.length];
            predictionDate = new String[setValuesList.length];
            int counter = 0;
            for (SetValues setValues : setValuesList) {
                //System.out.println(setValues.nnObj.desc);
                testNetworkHelper(setValues, counter);
                counter++;
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

    private void setValuesInNetwork(SetValues setValues) {
        int count = 0;
        //for each input val: assign to input node - Same amount of nodes as input val
        for (double d : setValues.inputVals) {
            this.inputNodes[count].inputValue = d;
            count++;
        }

        for (int j = 0; j < nodes[nodes.length - 1].length; j++) {
            Node node = nodes[nodes.length - 1][j];
            if (node instanceof OutputNeuron) {
                ((OutputNeuron) node).target = setValues.outputVals[j];
            }
        }
    }

    public void testNetworkHelper(SetValues setValues, int counter00) {

        this.testCountTotal++;
        setValuesInNetwork(setValues);

        System.out.println("***NODE OUTPUT CALC***");
        for (int count2 = 0; count2 < nodes.length; count2++) {
            for (Node node : nodes[count2]) {
                node.calculateNodeOutput(false, true);
            }
        }
        System.out.println(setValues);
        boolean tester = true;
        for (Node outputNeuron : nodes[nodes.length - 1]) {
            if (outputNeuron instanceof OutputNeuron) {
                System.out.println("Output:" + outputNeuron.tempOutput);
                predictionValueActual[totalCount_RESETBEFOREPREDICITION] = outputNeuron.tempOutput;
                ((Stock) testObjs[counter00]).predictedPrice = outputNeuron.tempOutput;
                predictionValueExpected[totalCount_RESETBEFOREPREDICITION] = ((OutputNeuron) outputNeuron).target;
                predictionDate[counter00] = ((Stock) testObjs[totalCount_RESETBEFOREPREDICITION]).getDate();

                if (checkOutputAgainstTarget_TESTING((OutputNeuron) outputNeuron)) {
                    System.out.print("-----------------------------------PASSED");
                    passCount++;
                } else {
                    tester = false;
                    System.out.print("-----------------------------------FAILED");
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

    public void exampleTrainingSet() {
        SetValues[] setValues = new SetValues[4];
        setValues[0] = new SetValues(0.1, 0.9, 0.9);
        setValues[1] = new SetValues(0.9, 0.1, 0.9);
        setValues[2] = new SetValues(0.1, 0.1, 0.1);
        setValues[3] = new SetValues(0.9, 0.9, 0.1);

        trainNetwork(setValues);
    }

    public SetValues[] exampleTrainingSet2() {
        SetValues[] setValues = new SetValues[4];
        setValues[0] = new SetValues(new double[]{0.1, 0.9}, new double[]{0.9});
        setValues[1] = new SetValues(new double[]{0.9, 0.1}, new double[]{0.9});
        setValues[2] = new SetValues(new double[]{0.1, 0.1}, new double[]{0.1});
        setValues[3] = new SetValues(new double[]{0.9, 0.9}, new double[]{0.1});

        this.setValuesList = setValues;
        setRMSE();

        return setValues;
    }

    public SetValues[] exampleTestingSet_GENERATED() {
        SetValues[] setValues = new SetValues[testCount];
        for (int i = 0; i < setValues.length; i++) {
            int randSelect = random.nextInt(4) + 1;
            SetValues setValuesTemp = null;
            if (randSelect == 1) {
                setValuesTemp = new SetValues(new double[]{0.1, 0.9}, new double[]{0.9});
            }
            if (randSelect == 2) {
                setValuesTemp = new SetValues(new double[]{0.9, 0.1}, new double[]{0.9});
            }
            if (randSelect == 3) {
                setValuesTemp = new SetValues(new double[]{0.1, 0.1}, new double[]{0.1});
            }
            if (randSelect == 4) {
                setValuesTemp = new SetValues(new double[]{0.9, 0.9}, new double[]{0.1});
            }
            setValues[i] = setValuesTemp;
        }

        return setValues;
    }

    public void testNetwork() {

        for (int nC = 0; nC < nodes.length; nC++) {
            for (Node node : nodes[nC]) {
                node.calculateNodeOutput(false, true);
            }
        }

        printInputValues();

        boolean tester = true;
        for (Node outputNeuron : nodes[nodes.length - 1]) {
            if (outputNeuron instanceof OutputNeuron) {
                System.out.println("Output:" + outputNeuron.tempOutput);

                if (checkOutputAgainstTarget_TESTING((OutputNeuron) outputNeuron)) {
                    System.out.print("-----------------------------------PASSED");
                } else {
                    tester = false;
                    System.out.print("-----------------------------------FAILED");
                }
            }
            System.out.println("");
        }

        if (!tester) {
            System.out.println("THIS TEST HAS FAILED ON ONE OR MORE OUTPUTS");
        } else {
            System.out.println("!!!!!!!!!!!!!!!!!PASSED TEST!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        System.out.println("");

    }

    private void printInputValues() {
        System.out.print("INPUT VALUES");
        for (InputNode inputNode : inputNodes) {
            System.out.print(" : " + inputNode.inputValue);
        }
        System.out.println("----");
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

    public void testExampleNetwork() {

        //STORE INPUT NODE VALS
        double[] initialVals = new double[inputNodes.length];
        for (int i = 0; i < initialVals.length; i++) {
            initialVals[i] = inputNodes[i].inputValue;
        }


        for (int x = 0; x < testCount; x++) {
            for (int i = 0; i < inputNodes.length; i++) {
                int randomInt = random.nextInt(10);
                double val = 0.1;
                if (randomInt >= 5) {
                    val = 0.9;
                }

                inputNodes[i].inputValue = val;
            }

            //for each output, give output as percentage and if desired one is highest, and all others are low, pass

            testNetwork();
        }

        //REVERT INPUT NODE VALUES
        for (int i = 0; i < initialVals.length; i++) {
            inputNodes[i].inputValue = initialVals[i];
        }

        resetNodeTempOutputs();
    }


    ////UPDATE TO HAVE TEST NETWORK USE SETVALUES OBJ
    public void testExampleNetwork2() {

        //STORE INPUT NODE VALS
        double[] initialVals = new double[inputNodes.length];
        for (int i = 0; i < initialVals.length; i++) {
            initialVals[i] = inputNodes[i].inputValue;
        }


        for (int x = 0; x < testCount; x++) {
            for (int i = 0; i < inputNodes.length; i++) {
                int randomInt = random.nextInt(10);
                double val = 0.1;
                if (randomInt >= 5) {
                    val = 0.9;
                }

                inputNodes[i].inputValue = val;
            }

            int count = 0;
            if (inputNodes[0].inputValue == inputNodes[1].inputValue) {
                for (Node outputNode : nodes[nodes.length - 1]) {
                    if (outputNode instanceof OutputNeuron) {
                        if (count == 0) {
                            ((OutputNeuron) outputNode).target = 0.1;
                        } else {
                            ((OutputNeuron) outputNode).target = 0.9;
                        }
                    }
                    count++;
                }
            } else {
                Node topOutputNode = nodes[nodes.length - 1][0];
                Node bottomOutputNode = nodes[nodes.length - 1][1];
                if (topOutputNode instanceof OutputNeuron) {
                    ((OutputNeuron) topOutputNode).target = 0.9;
                }
                if (bottomOutputNode instanceof OutputNeuron) {
                    ((OutputNeuron) bottomOutputNode).target = 0.1;
                }
            }

            testNetwork();
        }

        //REVERT INPUT NODE VALUES
        for (int i = 0; i < initialVals.length; i++) {
            inputNodes[i].inputValue = initialVals[i];
        }

        resetNodeTempOutputs();
    }

    public void resetNodeTempOutputs() {
        for (int nC = 0; nC < nodes.length; nC++) {
            for (Node node : nodes[nC]) {
                node.tempOutput = -1;
            }
        }

    }

    public void setupNetworkAndRunExample() {

        setupNetworkBase(1, 2, 1, 2, 1);

        //TRAINING
        int testingCount = 0;
        boolean endTesting = false;
        while (endTesting == false) {
            passCount = 0;
            failCount = 0;
            trainNetwork(exampleTrainingSet2());
            //TESTING
            testNetwork(exampleTestingSet_GENERATED());
            if (testingCount >= 4 || passCount / (passCount + failCount) > 0.9) {
                endTesting = true;
            }
            testingCount++;
            passCountTotal += passCount;
            failCountTotal += failCount;
        }

        System.out.println("TESTING COUNT: " + testingCount);
    }

    /**
     * Setup the network and nodes along with connections and base data
     *
     * @param hiddenNeuronLayers
     * @param hiddenNeuronCount
     * @param biasVal
     * @param inputCount
     * @param outputCount
     */
    private void setupNetworkBase(int hiddenNeuronLayers, int hiddenNeuronCount, double biasVal, int inputCount, int outputCount) {

        this.hiddenNeuronLayersCount = hiddenNeuronLayers;
        this.hiddenNeuronCount = hiddenNeuronCount;
        nodes = new Node[hiddenNeuronLayers + 1][];

        createInputLayer(inputCount);
        createHiddenNeurons(biasVal);
        createOutputLayer(outputCount, biasVal);
        setupConnections();

        //setRMSE();

    }

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
            outputNeurons[count] = new OutputNeuron(biasVal, randomDouble(), 0.9);//TODO CHANGE BACK TO RANDOM DOUBLE FOR SECOND PARAM
            count++;
        }

        nodes[nodes.length - 1] = outputNeurons;
    }

    //ONLY WORKS FOR ONE HIDDEN LAYER
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

    public void setupNetwork() {
        setupNetwork(nnObjs, testObjs);
    }

    private SetValues[] updateSetValuesResults(NNObj[] objs) {

        SetValues[] setValuesList = new SetValues[objs.length];
        for (int i = 0; i < objs.length; i++) {
            objs[i].createSetValues();
            setValuesList[i] = objs[i].getSetValues();
        }

        return setValuesList;
    }

    private SetValues[] updateSetValuesResults_Symbols(NNObj[] objs) {

        SetValues[] setValuesList = new SetValues[objs.length];
        for (int i = 0; i < objs.length; i++) {

            double[] tempResultsSet = new double[objs.length];
            for (int t = 0; t < tempResultsSet.length; t++) {
                tempResultsSet[t] = 0.0;
            }
            tempResultsSet[objs[i].id] = 1.0;
            setValuesList[i] = new SetValues(objs[i].dVals, tempResultsSet, objs[i]);
        }

        return setValuesList;
    }

    public void updateErrorSignals() {
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

    public void updateWeights() {
        for (int nC = 0; nC < nodes.length; nC++) {
            for (Node node : nodes[nC]) {
                node.updateWeights(learningRate);
            }
            epoch++;
        }
    }

    public double randomDouble() {
        double d = random.nextDouble();
//        d = d * 0.99 *100;
//        int temp = (int) d;
//        d = temp/100.0;

        if (random.nextInt(2) != 1) {
            d = d * -1;
        }

        return d;

    }

}
