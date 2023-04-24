package common.implementations.models.FeedForward;

import common.implementations.models.FeedForward.components.NNObj;

public class Symbol extends NNObj {

    public Symbol(int[] newVals, int[] targetVals, String desc) {
        dVals_targets = convertValToDouble(targetVals);
        dVals = convertValToDouble(newVals);
        description = desc;
    }

    /**
     * Method to take an array of 1s and 0s and convert them to 0.9 and 0.1 respectively
     *
     * @param vals
     * @return
     */
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
