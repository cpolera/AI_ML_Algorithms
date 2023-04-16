package models.FeedForward;

import models.FeedForward.components.NNObj;

public class Symbol extends NNObj {

    protected int[] vals;

    public Symbol(int[] newVals, int[] targetVals, String desc) {
        vals = newVals;
        dVals_targets = convertValToDouble(targetVals);
        dVals = convertValToDouble(vals);
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
