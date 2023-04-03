package algorithms.NearestNeighbor;

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

            // TODO: implement algorithm based on grid. Bonus since this will facilitate multithreading
            // Prepare objects in an excruciating way
            updateDistances(neighbors, vertex); // O(n) - updates all nodes with distance to current iterable yikes
            Collections.sort(neighbors); // Should mean that current one is first in the list

            ArrayList<ObjectNN> nearestNeighbors = new ArrayList<>();
            ArrayList<String> foundClassifications = new ArrayList<>();
            for (int i = 1; i < k; i++) { // i=0 is current iteration, so start at 1
                //Grab closest ones // O(k)
                nearestNeighbors.add(neighbors.get(i));
                // Get each classification store it separately?!
                foundClassifications.add(neighbors.get(i).classPlaceholder);
            } // Result: list of closest neighbors and list of each neighbors' classification

            // TODO: implement algorithm to get bucket counts for each classifier. below is brute force x 3.
            HashSet<String> conversionSet = new HashSet<>(foundClassifications); // Believe this just compresses list
            foundClassifications = new ArrayList<>(conversionSet); // gets list back?

            int[] classifierCounts = new int[foundClassifications.size()]; // Number of unique classifiers

            // For each unique classifier found at nearby neighbor
            for (int i = 0; i < foundClassifications.size(); i++) { // O(k)
                int classifierCount = 0;
                // For each neighbor nearby
                for (ObjectNN objectNN : nearestNeighbors) { // O(k)
                    // add count if classifier matches given classifier?!
                    if (objectNN.classPlaceholder.equals(foundClassifications.get(i))) {
                        classifierCount++;
                    }
                }
                classifierCounts[i] = classifierCount; // map count to classifier index
            }

            // go through each count and find largest?!!
            int indexOfLargestCount = 0;
            for (int i = 1; i < classifierCounts.length; i++) { // O(k)
                if (classifierCounts[i] > classifierCounts[indexOfLargestCount]) {
                    indexOfLargestCount = i;
                }
            }

            vertex.classPlaceholder = foundClassifications.get(indexOfLargestCount);

            System.out.println("The class of the unknown object is: " + vertex.classPlaceholder);
            System.out.println("xVAl " + vertex.vector.x + "yVAl " + vertex.vector.y);
        }
    }

    /**
     * Incredible function that mutates parameters. SHAME
     *
     * @param neighbors
     * @param unknownObject
     */
    public static void updateDistances(ArrayList<ObjectNN> neighbors, ObjectNN unknownObject) {
        for (ObjectNN objectNN : neighbors) {
            objectNN.calcDistance(unknownObject);
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
