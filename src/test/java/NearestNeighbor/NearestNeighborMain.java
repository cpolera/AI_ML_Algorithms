package NearestNeighbor;

import common.Vector;
import common.implementations.models.NearestNeighbor.ObjectNN;

import java.io.IOException;
import java.util.ArrayList;

import static common.implementations.models.NearestNeighbor.NearestNeighborClassifier.classifyNeighbors;

/**
 * Classification algorithm also known as k-nearest neighbors algorithm
 */
public class NearestNeighborMain {

    static ArrayList<ObjectNN> neighbors = new ArrayList<>();
    static int k = 5;

    public static void main(String[] args) throws IOException {
        initDataSpecific();
        classifyNeighbors(neighbors, k);
    }

    public static void initDataSpecific(){
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
    }
}
