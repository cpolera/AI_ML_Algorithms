package algorithms.NearestNeighbor;

import java.io.IOException;
import java.util.*;

/**
 * Classification algorithm also known as k-nearest neighbors algorithm
 */
public class NearestNeighborMain {

    // Test datasets
    static double[] yVals = new double[]{10.1, 10, 8.3, 9.0, 8.9, 8.1, 8.3, 7.9, 7.9, 8.1, 7.8, 7.5, 8.1, 8.3, 8.4, 8.9}; //16
    static double[] xVals = new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}; //16
    static boolean prepopulateClassifiers = true;

    static ArrayList<ObjectNN> neighbors = new ArrayList<>();

    static int k = 5;

    public static void main(String[] args) throws IOException {
        // Initialize nodes and neighborhood
        initData();

        classifyData(neighbors);
    }

    public static void classifyData(ArrayList<ObjectNN> neighbors){
        for (ObjectNN vertex : neighbors) { // O(n)

            // Get neighbors within distance
            ArrayList<String> foundClassifications = new ArrayList<>();
            HashMap<String, Integer> map = new HashMap<>();
            for(int i=0; i < neighbors.size(); i++){
                ObjectNN neighbor = neighbors.get(i);
                if(neighbor.id != vertex.id && vertex.calcDistance(neighbor) < k){ // Skip if same one
                    foundClassifications.add(neighbor.classification);
                    map.merge(neighbor.classification, 1, Integer::sum);
                }
            }

            int max = Collections.max(map.values());
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() == max) {
                    vertex.classification = entry.getKey();
                    break;
                }
            }

            System.out.println("The class of the unknown object is: " + vertex.classification);
            System.out.println("xVAl " + vertex.vector.x + "yVAl " + vertex.vector.y);
        }
    }

    public static void initData() {
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
            neighbors.add(new ObjectNN(xVals[i], yVals[i], classPlaceholders[i]));
        }
    }
}
