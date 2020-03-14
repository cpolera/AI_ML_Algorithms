package Feedfoward;

public class Symbol extends NNObj {

    protected int[] vals;
    static int incr = 0;

    public Symbol(int[] newVals, int[] targetVals, String desc) {
        this.id = incr;
        vals = newVals;
        dVals_targets = convertValToDouble(targetVals);
        dVals = convertValToDouble(vals);
        incr++;
        this.desc = desc;
    }

    public double[] convertValToDouble(int[] vals) {
        double[] newVals = new double[vals.length];

        int count = 0;
        for (int val : vals) {
            if (val == 0) {
                newVals[count] = val + 0.1;
            }
            if (val == 1) {
                newVals[count] = val - 0.1;
            }
            count++;
        }

        return newVals;
    }
}
