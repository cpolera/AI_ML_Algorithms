package models.GeneticAlgorithm.company;

import java.io.*;
import java.util.*;

/**
 * Apr 20, 2023: This code is over 5 years old and needs to be heavily refactored. Lots of novice mistakes and issues
 *
 * Permutations are isolated which means this would benefit from multithreading or something similar
 *
 * Improvements:
 * If a certain subset cost is worse than the best permutation overall cost thus far, skip all other iterations with that pattern
 * requires generating permutation and checking cost before moving on to generating the next one.
 *
 */
public class MainQAPSolution {

    int[][] flowMatrix; // required flow between facilities
    int[][] distanceMatrix; // distance between locations
    String file; // Put at root of project for now //had20.txt is all facilities go to all other facilities

    HashMap<Integer, ArrayList<int[]>> results = new HashMap<Integer, ArrayList<int[]>>();
    int[] currentFacilityPermutation; // Location values start at 1; 0 is used for special case
    int now = -1;

    int min = 0;
    int max = 0;
    int[] bestPermutation;
    int[] worstPermutation;

    long startTime;
    long estimatedTime;

    public void main() throws IOException {
        startTime = System.nanoTime();
        log("Starting evaluation...", 1);

        readInData(this.file);
        currentFacilityPermutation = new int[getLocationCount()];
        generateAndProcessPermutations(0);

        estimatedTime = System.nanoTime() - startTime;
        log("Completed evaluation.", 1);

        consoleReport();
        String filename = "results_" + this.file +".txt";
        logToOutputFile(filename);
    }

    /**
     * This was not documented before so the following is my best understanding after staring at the code
     *
     *
     *
     * @param k
     */
    public void generateAndProcessPermutations(int k) {
        now++;
        currentFacilityPermutation[k] = now;
        if (now == getLocationCount()){
            processPermutation();
        }
        for (int i = 0; i < getLocationCount(); i++)
            if (currentFacilityPermutation[i] == 0) {
                generateAndProcessPermutations(i);
            }
        now--;
        currentFacilityPermutation[k] = 0;
    }

    /**
     * Calculates the cost of each permutation
     */
    public void processPermutation() {
        int[] permutation = currentFacilityPermutation.clone();

        int permutationCost = calculateCost(permutation);

        int permutationCount = countResults(getResults());
        log(permutationCount + " ** " + getPermutationString(permutation) + " : " + permutationCost + "**", 2);
    }

    /**
     * Updates related variables as needed based on the latest permutation cost
     *
     * @param permutation
     * @param permutationCost
     */
    private void handleCost(int[] permutation, int permutationCost) {
        if (min == 0 || min >= permutationCost) {
            min = permutationCost;
            bestPermutation = permutation; // TODO does not handle multiple optimal solutions
        }
        if (max < permutationCost) {
            max = permutationCost;
            worstPermutation = permutation;
        }

        results.computeIfAbsent(permutationCost, k -> new ArrayList<>());
        results.get(permutationCost).add(permutation);
    }

    /**
     * Calculates the cost of a given facility permutation and updates relevant metrics
     *
     * @param permutation
     * @return
     */
    public int calculateCost(int[] permutation) {
        int cost = 0;
        // Locations start at 1, so we subtract 1 to get the corresponding facility index
        for (int i = 0; i < permutation.length; i++) { // Get each assigned location
            int facilityIndex = permutation[i] - 1;
            for (int j = 0; j < permutation.length; j++) { // TODO: something isnt calculating correctly here
                int receivingFacilityIndex = permutation[j] - 1;
                int distance = distanceMatrix[i][j]; // d(location i, location j)
                int flow = flowMatrix[facilityIndex][receivingFacilityIndex]; // f(facility i, facility j)
                if(flow > 0 && distance > 0){ // only include actual flows and separate locations
                    int subCost = (distance * flow);
                    log(
                            "Distance between " + permutation[i] + " & " + permutation[j]
                                    + ": " + distance + "\n" + "Flow: " + flowMatrix[facilityIndex][receivingFacilityIndex]
                                    + "\n" + "Cost: " + subCost,
                            3
                    );
                    cost += subCost;
                }
            }
        }
        handleCost(permutation, cost);
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
        log("Min cost : " + min + " ::::: Permutation is " + getPermutationString(bestPermutation), 1);
        log("Max cost : " + max + " ::::: Permutation is " + getPermutationString(worstPermutation), 1);
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

    public int[][] generateMatrix(int[] list) {
        int numOfObjects = (int) Math.sqrt(list.length);
        int[][] matrix = new int[numOfObjects][numOfObjects];
        int counter = 0;
        for (int i = 0; i < numOfObjects; i++) {
            for (int j = 0; j < numOfObjects; j++) {
                matrix[i][j] = list[counter];
                counter++;
            }
        }
        return matrix;
    }

    private void logToOutputFile(String filename) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        double seconds = (double) estimatedTime / 1000000000.0;
        bufferedWriter.write("Time to calculate in seconds: " + seconds);
        bufferedWriter.newLine();
        bufferedWriter.write("Min cost : " + min + " ::::: Permutation is " + getPermutationString(bestPermutation));
        bufferedWriter.newLine();
        bufferedWriter.write("Max cost : " + max + " ::::: Permutation is " + getPermutationString(worstPermutation));
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

    public void setFile(String filePath) {
        this.file = filePath;
    }

    public HashMap<Integer, ArrayList<int[]>> getResults() {
        return this.results;
    }

    public int[] getBestPermutation() {
        return this.bestPermutation;
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