package NearestNeighbor;

import com.implementations.models.common.Vector;
import com.implementations.models.NearestNeighbor.ObjectNN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class NearestNeighborTestHelper {

    public static ArrayList<ObjectNN> initData(boolean prepopulateClassifiers) {
        ArrayList<ObjectNN> neighbors = new ArrayList<>();

        // Test datasets
        double[] yVals = new double[]{10.1, 10, 8.3, 9.0, 8.9, 8.1, 8.3, 7.9, 7.9, 8.1, 7.8, 7.5, 8.1, 8.3, 8.4, 8.9}; //16
        double[] xVals = new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}; //16

        // Initial classifier for all nodes
        String[] classPlaceholders;
        if(prepopulateClassifiers){
            classPlaceholders = new String[]{"b", "s", "s", "h", "s", "h", "b", "b", "b", "s", "s", "h", "s", "h", "b", "b"};
        } else {
            classPlaceholders = new String[16]; // start with unknown instead for all of them
            Arrays.fill(classPlaceholders, "unknown");
        }

        // Generate neighborhood
        for (int i = 0; i < xVals.length; i++) {
            neighbors.add(new ObjectNN(new Vector(xVals[i], yVals[i]), classPlaceholders[i]));
        }
        // Shuffle for more randomized order
        Collections.shuffle(neighbors);
        return neighbors;
    }

    public static ArrayList<ObjectNN> initData(int nodeCount){
        ArrayList<ObjectNN> neighbors = new ArrayList<>();

        for(int i=0; i< nodeCount; i++){
            double random = Math.random() * 100;
            String s = i%2 > 0 ? "O" : "X";
            neighbors.add(new ObjectNN(new Vector(Math.random() * 10, Math.random() * 10), s));
        }
        return neighbors;
    }

    public static ArrayList<ObjectNN> initDataSpecific(){
        ArrayList<ObjectNN> neighbors = new ArrayList<>();

        // Set two in farthest corners
        neighbors.add(new ObjectNN(new Vector(0,0), "O"));
        neighbors.add(new ObjectNN(new Vector(100,100), "X"));

        // Generate neighborhood near 0,0
        neighbors.add(new ObjectNN(new Vector(0,0.1), "X"));
        neighbors.add(new ObjectNN(new Vector(0.1,0), "X"));
        neighbors.add(new ObjectNN(new Vector(0.15,0), "X"));
        neighbors.add(new ObjectNN(new Vector(0,0.15), "X"));
        neighbors.add(new ObjectNN(new Vector(0.15,0.15), "X"));

        // Generate neighborhood near 100,100
        neighbors.add(new ObjectNN(new Vector(99,99.1), "O"));
        neighbors.add(new ObjectNN(new Vector(99.1,99), "O"));
        neighbors.add(new ObjectNN(new Vector(99.15,99), "O"));
        neighbors.add(new ObjectNN(new Vector(99,99.15), "O"));
        neighbors.add(new ObjectNN(new Vector(99.15,99.15), "O"));

        return neighbors;
    }
}
