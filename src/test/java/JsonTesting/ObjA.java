package JsonTesting;

public class ObjA {

    private static int ID = 0;
    private int id;
    private ObjB objB;

    ObjA() {
        id = ID;
        ID++;
        objB = new ObjB(id);
    }

    public String toString(){
        return "ID: " + id;
    }
}
