package Logging;

public class LogObject1 {

    private static int _id = 0;
    private double _d = 0.1;
    private String _s = "test";

    public LogObject1(){
        _id++;
    }

    public void setD(double d){ _d = d;}

    public void setS(String s){_s = s;}
}
