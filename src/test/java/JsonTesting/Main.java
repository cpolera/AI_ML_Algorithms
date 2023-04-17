package JsonTesting;

import models.FeedForward.components.Network;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class Main {


    public static void main(String[] args) throws IOException {

        ObjA obj1 = new ObjA();
        ObjA obj2 = new ObjA();
        ObjA obj3 = new ObjA();
        ObjA obj4 = new ObjA();

        ArrayList<ObjA> objs  = new ArrayList<>();

        objs.add(obj1);
        objs.add(obj2);
        objs.add(obj3);
        objs.add(obj4);

        Gson gson = new Gson();
        System.out.println(gson.toJson(objs));

        Network network = new Network();
        System.out.println(gson.toJson(network));

    }
}
