package Logging;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class LogObject1 {

    private static int _idCounter = 0;

    @Expose private int _id;
    @Expose private double _d = 0.1;
    @Expose private String _s = "test";
    @Expose private ArrayList<LogObject2> logObject2ArrayList = new ArrayList<>();

    public LogObject1(){
        _id = _idCounter;
        _idCounter++;

        for(int i = 0; i < 10; i++){
            logObject2ArrayList.add(new LogObject2());
        }
    }

    public void setD(double d){ _d = d;}

    public void setS(String s){_s = s;}

}
