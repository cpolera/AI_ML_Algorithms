package JsonTesting;

public class ObjB {

    private String x;

    public ObjB(int i){
        x = i + ": TEST " + i;
    }

    public String toString(){
        return x;
    }
}
