package Feedfoward;

public class NNObj {

    public String desc;
    protected int[] vals;
    protected double[] dVals;
    protected double[] dVals_targets;
    protected SetValues setValues;
    protected int id;

    public NNObj() {
    }


    public SetValues getSetValues() {
        return setValues;
    }

    public SetValues createSetValues() {
        setValues = new SetValues(dVals, dVals_targets, this);
        return setValues;
    }
}
