package models.QuadraticAssignment;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

import static models.QuadraticAssignment.QAPUtility.generateMatrix;

/**
 * Permutations are isolated which means this would benefit from multithreading or something similar
 *
 * Improvements:
 * If a certain subset cost is worse than the best permutation overall cost thus far, skip all other iterations with that pattern
 * requires generating permutation and checking cost before moving on to generating the next one.
 *
 */
public class QAP {

    private int[][] flowMatrix; // required flow between facilities
    private int[][] distanceMatrix; // distance between locations
    private String file; // Put at root of project for now //qap20.txt is all facilities go to all other facilities

    private final HashMap<Integer, ArrayList<int[]>> results = new HashMap<Integer, ArrayList<int[]>>();
    private int[] currentFacilityPermutation;
    private int nextFacilityId = -2; // Needs to start at -2

    private int min = 0;
    private int max = 0;

    private long estimatedTime;

    public QAP(String filename){
        this.file = filename;
    }

    public void runSolution() throws IOException {
        long startTime = System.nanoTime();
        log("Starting evaluation...", 1);

        readInData(this.file);
        currentFacilityPermutation = new int[getLocationCount()];
        Arrays.fill(currentFacilityPermutation, -1);
        generateAndProcessPermutations(0);

        estimatedTime = System.nanoTime() - startTime;
        log("Completed evaluation.", 1);

        consoleReport();
        String filename = "results_" + this.file;
        logToOutputFile(filename);
    }

    /**
     * Generates permutations and processes them as it goes
     *
     *  This updates the currentFacilityPermutation
     *  A value of -1 at any index  of the permutation indicates it needs the nextFacilityId
     *
     * @param k index to place nextFacilityId
     */
    private void generateAndProcessPermutations(int k) {
        log("Starting nextFacilityId: " + nextFacilityId + "\t\t", 4);
        nextFacilityId++;
        log("Pre-interim permutation:\t " + getPermutationString(currentFacilityPermutation) + "\tk: " + k + "\tnextFacilityId: " + nextFacilityId, 4);
        currentFacilityPermutation[k] = nextFacilityId;
        log("interim permutation:\t\t " + getPermutationString(currentFacilityPermutation), 4);

        if (nextFacilityId == getLocationCount()-1){
            int[] permutation = currentFacilityPermutation.clone();
            System.out.print ("nextFacilityId: " + nextFacilityId);
            log("  k: " + k + "  permutation: " + getPermutationString(permutation), 4);
            processPermutation(permutation);
        }

        for (int i = 0; i < getLocationCount(); i++){
            if (currentFacilityPermutation[i] == -1) {
                generateAndProcessPermutations(i);
            }
        }

        nextFacilityId--;
        currentFacilityPermutation[k] = -1;
    }

    private void processPermutation(int[] permutation) {
        int permutationCost = calculateCost(permutation);

        int permutationCount = countResults(getResults());
        log(permutationCount + " ** " + getPermutationString(permutation) + " : " + permutationCost + "**", 2);
    }

    private void updateCostMetrics(int[] permutation, int permutationCost) {
        if (min == 0 || min >= permutationCost) {
            min = permutationCost;
        }
        if (max < permutationCost) {
            max = permutationCost;
        }

        results.computeIfAbsent(permutationCost, k -> new ArrayList<>());
        results.get(permutationCost).add(permutation);
    }

    /**
     * Calculates the cost of a given facility permutation and updates relevant metrics
     *
     * @param permutation array of Facility IDs
     * @return
     */
    public int calculateCost(int[] permutation) {
        int cost = 0;
        Map<Integer, HashSet<Integer>> allLocationsVisited = new HashMap<>();
        for (int locationI = 0; locationI < permutation.length; locationI++) {
            HashSet<Integer> thisLocationVisited = new HashSet<>();
            thisLocationVisited.add(locationI);
            allLocationsVisited.put(locationI, thisLocationVisited);
            for (int locationJ = 0; locationJ < permutation.length; locationJ++) {
                if(!allLocationsVisited.containsKey(locationJ) || !allLocationsVisited.get(locationJ).contains(locationI)){ // Only for separate locations
                    int facilityIndex = permutation[locationI]; // Facility I at location I
                    int receivingFacilityIndex = permutation[locationJ]; // Facility J at location J

                    int distance = distanceMatrix[locationI][locationJ]; // d(location i, location j)
                    int flow = flowMatrix[facilityIndex][receivingFacilityIndex]; // f(facility i, facility j)
                    if(flow > 0 && distance > 0){ // only include actual flows and known distances
                        int subCost = (distance * flow);
                        log(
                                "Distance between " + permutation[locationI] + " & " + permutation[locationJ]
                                        + ": " + distance + "\n" + "Flow: " + flowMatrix[facilityIndex][receivingFacilityIndex]
                                        + "\n" + "subCost: " + subCost,
                                3
                        );
                        cost += subCost;
                    }
                }
                thisLocationVisited.add(locationJ);
            }
        }
        updateCostMetrics(permutation, cost);
        return cost;
    }

    public String getPermutationString(int[] permutation) {
        String returnString = "";
        for (int aPermutation : permutation) {
            returnString = returnString + " " + aPermutation;
        }
        return returnString;
    }

    public void consoleReport() {
        double seconds = (double) estimatedTime / 1000000000.0;
        log("Time to calculate in seconds: " + seconds, 1);
        log("Min cost : " + min + " ::::: Permutation is " + getPermutationString(getBestPermutation()), 1);
        log("Max cost : " + max + " ::::: Permutation is " + getPermutationString(getWorstPermutation()), 1);
        log("Total permutations ran: " + countResults(getResults()), 1);
    }

    /**
     * Setup the flow and distance matrices
     * File should be txt with data as so:
     * --
     * 2
     *
     * 0 1
     * 1 0
     *
     * 3 5
     * 3 9
     * --
     * @throws FileNotFoundException
     */
    public void readInData(String filename) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filename));
        int size = sc.nextInt();
        int[][] inputs = new int[2][size * size];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < size * size; j++) {
                inputs[i][j] = sc.nextInt();
            }
        }

        flowMatrix = generateMatrix(inputs[1]);
        distanceMatrix = generateMatrix(inputs[0]);

        sc.close();
    }

    public void setFile(String filePath) {
        this.file = filePath;
    }

    public HashMap<Integer, ArrayList<int[]>> getResults() {
        return this.results;
    }

    public int[] getBestPermutation() {
        return getBestPermutations().get(0);
    }

    public ArrayList<int[]> getBestPermutations() {
        return results.get(min);
    }

    public int[] getWorstPermutation() {
        return getWorstPermutations().get(0);
    }

    public ArrayList<int[]> getWorstPermutations() {
        return results.get(max);
    }

    public int getLocationCount() {
        return distanceMatrix.length;
    }

    public int countResults() {
        return countResults(getResults());
    }

    public int countResults(HashMap<Integer, ArrayList<int[]>> results) {
        int count = 0;
        for (Map.Entry<Integer, ArrayList<int[]>> entry : results.entrySet()) {
            count += entry.getValue().size();
        }
        return count;
    }

    private void logToOutputFile(String filename) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        double seconds = (double) estimatedTime / 1000000000.0;
        bufferedWriter.write("Time to calculate in seconds: " + seconds);
        bufferedWriter.newLine();
        bufferedWriter.write("Min cost : " + min + " ::::: Permutation is " + getPermutationString(getBestPermutation()));
        bufferedWriter.newLine();
        bufferedWriter.write("Max cost : " + max + " ::::: Permutation is " + getPermutationString(getWorstPermutation()));
        bufferedWriter.newLine();
        bufferedWriter.write("Total permutations ran: " + countResults(getResults()));
        bufferedWriter.newLine();
        for (Map.Entry<Integer, ArrayList<int[]>> entry : results.entrySet()) {
            Integer key = entry.getKey();
            int value = entry.getValue().size();
            log("Result Value: " + key + " ... Count: " + value, 3);
            String outputString = "" + key + ", " + value;
            bufferedWriter.newLine();
            bufferedWriter.write(outputString);
        }
        bufferedWriter.newLine();
        bufferedWriter.newLine();

        bufferedWriter.close();
    }

    public static void log(String logString, int debugLevel){
        if(debugLevel <= Integer.parseInt(System.getProperty("QAP_DEBUG_LEVEL"))){
            System.out.println(logString);
        }
    }
}
//


///3 10 11 2 12 5 6 7 8 1 4 9
///3 10 11 2 12 5 7 6 8 1 4 9
//          0  1  2  3  4  5  6  7  8  9  10 11
//=================================================
//          0  1  2  2  3  4  4  5  3  5  6  7      0
//          1  0  1  1  2  3  3  4  2  4  5  6      1
//          2  1  0  2  1  2  2  3  1  3  4  5      2
//          2  1  2  0  1  2  2  3  3  3  4  5      3
//          3  2  1  1  0  1  1  2  2  2  3  4      4
//          4  3  2  2  1  0  2  3  3  1  2  3      5
//          4  3  2  2  1  2  0  1  3  1  2  3      6
//          5  4  3  3  2  3  1  0  4  2  1  2      7
//          3  2  1  3  2  3  3  4  0  4  5  6      8
//          5  4  3  3  2  1  1  2  4  0  1  2      9
//          6  5  4  4  3  2  2  1  5  1  0  1      10
//          7  6  5  5  4  3  3  2  6  2  1  0      11
//
//          0  3  4  6  8  5  6  6  5  1  4  6
//          3  0  6  3  7  9  9  2  2  7  4  7
//          4  6  0  2  6  4  4  4  2  6  3  6
//          6  3  2  0  5  5  3  3  9  4  3  6
//          8  7  6  5  0  4  3  4  5  7  6  7
//          5  9  4  5  4  0  8  5  5  5  7  5
//          6  9  4  3  3  8  0  6  8  4  6  7
//          6  2  4  3  4  5  6  0  1  5  5  3
//          5  2  2  9  5  5  8  1  0  4  5  2
//          1  7  6  4  7  5  4  5  4  0  7  7
//          4  4  3  3  6  7  6  5  5  7  0  9
//          6  7  6  6  7  5  7  3  2  7  9  0