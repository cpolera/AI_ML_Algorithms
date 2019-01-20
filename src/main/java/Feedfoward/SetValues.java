package Feedfoward;

public class SetValues {

    public double[] inputVals;
    public double[] outputVals; //match to number of possible results

    NNObj nnObj;

    public SetValues(double A, double B, double AXorB) {
        this.inputVals = new double[]{A, B};
        this.outputVals = new double[]{AXorB};
    }

    public SetValues(double[] vals, double[] AXorBList, NNObj nnObj) {
        this.inputVals = vals;
        this.outputVals = AXorBList;
        this.nnObj = nnObj;
    }

    public SetValues(double[] vals, double[] AXorBList) {
        this.inputVals = vals;
        this.outputVals = AXorBList;
    }


    public String toString() {
        String s = "Input Vals: ";
        for (double d : inputVals) {
            s = s + " " + d + " ";
        }
        s = s + "/n Output Vals: ";
        for (double d : outputVals) {
            s = s + " " + d + " ";
        }

        return s;
    }
}
