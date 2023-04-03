package algorithms.NearestNeighbor;

import implementations.FinalProjStocks.Stock;
import implementations.FinalProjStocks.StocksMain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Classification algorithm also known as k-nearest neighbors algorithm
 */
public class NearestNeighborMain {

    // Test datasets
    static double[] yVals = new double[]{10.1, 10, 8.3, 9.0, 8.9, 8.1, 8.3, 7.9, 7.9, 8.1, 7.8, 7.5, 8.1, 8.3, 8.4, 8.9}; //16
    static double[] xVals = new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}; //16

    static ArrayList<ObjectNN> neighbors = new ArrayList<>();
    static ArrayList<String> classes2 = new ArrayList<>();

    static int k = 5;
    static ObjectNN[] unknownObjects;

    public static void main(String[] args) throws IOException {
        // Initial naive classifier for all nodes
        String[] classPlaceholders = new String[]{"b", "s", "s", "h", "s", "h", "b", "b", "b", "s", "s", "h", "s", "h", "b", "b"};
        String[] unknownPlaceholders = new String[16]; // start with unknown instead for all of them
        Arrays.fill(unknownPlaceholders, "unknown");

        // Generate neighborhood
        for (int i = 0; i < xVals.length; i++) {
            neighbors.add(new ObjectNN(xVals[i], yVals[i], classPlaceholders[i]));
        }

        // Generate list of unknown objects (same as neighborhood above but with unknown instead?)
        unknownObjects = new ObjectNN[xVals.length];
        for (int i = 0; i < xVals.length; i++) {
            unknownObjects[i] = new ObjectNN(xVals[i], yVals[i], unknownPlaceholders[i]);
        }

        for (ObjectNN unknownObject : unknownObjects) { // O(n)
            updateDistances(neighbors, unknownObject); // O(n) - updates all nodes with distance to current iterable yikes
            Collections.sort(neighbors);

            //Grab closest ones // O(k)
            ArrayList<ObjectNN> nearestNeighbors = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                nearestNeighbors.add(neighbors.get(i));
            } // Result: list of closest neighbors

            //---
            //figure out count of unique classifiers and use one with most
            int counter = 0;
            for (ObjectNN objectNN : nearestNeighbors) { // O(k)
                classes2.add(counter, (objectNN.classPlaceholder));
                counter++;
            } // Result: list of each neighbors' classification in a new list!

            HashSet<String> conversionSet = new HashSet<>(classes2); // Believe this compresses list quickly with counts
            classes2 = new ArrayList<>(conversionSet); // gets list back?

            int[] classCount = new int[classes2.size()]; // Number of unique classifiers

            // For each unique classifier found at nearby neighbor
            for (int i = 0; i < classes2.size(); i++) { // O(k)
                int count1 = 0;
                // For each neighbor nearby
                for (ObjectNN objectNN : nearestNeighbors) { // O(k)
                    // add count if classifier matches given classifier?!
                    if (objectNN.classPlaceholder.equals(classes2.get(i))) {
                        count1++;
                    }
                }
                classCount[i] = count1;
            }

            // go through each count and find largest?!!
            int finalIndex = 0;
            for (int i = 1; i < classCount.length; i++) { // O(k)
                if (classCount[i] > classCount[finalIndex]) {
                    finalIndex = i;
                }
            }

            unknownObject.classPlaceholder = classes2.get(finalIndex);
            //--
            System.out.println("The class of the unknown object is: " + unknownObject.classPlaceholder);
            System.out.println("xVAl " + unknownObject.vector.x + "yVAl " + unknownObject.vector.y);

        }

    }

    public static void updateDistances(ArrayList<ObjectNN> neighbors, ObjectNN unknownObject) {
        for (ObjectNN objectNN : neighbors) {
            objectNN.calcDistance(unknownObject);
        }
    }
}
