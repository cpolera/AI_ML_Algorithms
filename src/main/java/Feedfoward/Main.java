package Feedfoward;


public class Main {

    public static void main(String args[]) {
        Network network = new Network();
        //network.setupNetworkAndRunExample();
        NNObj[] nnObjs = createSymbolList_EXAMPLE();
        NNObj[] nnObjs1 = createTestSymbols();

        network.desiredError = 0.1;
        network.setupNetwork(5, 1, nnObjs, nnObjs1);
    }

//update to take any number of inputs
    //update to have any number of output neurons
    //update to handle multiple layers of hidden neurons
    //update training method to handle any number of inputs/ same with testing


    private static Symbol[] createSymbolList_EXAMPLE() {

        Symbol.incr = 0;

        int[] plusIntoVals = new int[]{0, 0, 1, 0, 0,
                0, 0, 1, 0, 0,
                1, 1, 1, 1, 1,
                0, 0, 1, 0, 0,
                0, 0, 1, 0, 0};

        int[] xIntoVals = new int[]{1, 0, 0, 0, 1,
                0, 1, 0, 1, 0,
                0, 0, 1, 0, 0,
                0, 1, 0, 1, 0,
                1, 0, 0, 0, 1};

        int[] OintoVals = new int[]{
                1, 1, 1, 1, 1,
                1, 0, 0, 0, 1,
                1, 0, 0, 0, 1,
                1, 0, 0, 0, 1,
                1, 1, 1, 1, 1
        };

        Symbol[] symbols = new Symbol[]{new Symbol(plusIntoVals, new int[]{0, 0, 1}, "PLUS"), new Symbol(xIntoVals, new int[]{0, 1, 0}, "X"), new Symbol(OintoVals, new int[]{1, 0, 0}, "O")};

        return symbols;
    }

    private static Symbol[] createTestSymbols() {

        Symbol.incr = 0;

        int[] plusSymbol = new int[]{
                0, 0, 0, 0, 0,
                0, 0, 1, 0, 0,
                1, 0, 1, 1, 1,
                0, 0, 1, 0, 0,
                0, 0, 1, 0, 0};

        int[] xIntoVals = new int[]{
                1, 1, 0, 1, 1,
                0, 1, 0, 1, 0,
                0, 0, 1, 0, 0,
                0, 1, 0, 1, 0,
                1, 0, 0, 0, 1};

        int[] OintoVals = new int[]{
                1, 1, 1, 1, 1,
                1, 0, 0, 1, 1,
                1, 0, 0, 0, 1,
                1, 0, 0, 0, 0,
                1, 1, 1, 1, 1
        };
        Symbol[] symbols = new Symbol[]{new Symbol(plusSymbol, new int[]{0, 0, 1}, "PLUS_t"), new Symbol(xIntoVals, new int[]{0, 1, 0}, "X_t"), new Symbol(OintoVals, new int[]{1, 0, 0}, "O_t")};

        return symbols;
    }
}
