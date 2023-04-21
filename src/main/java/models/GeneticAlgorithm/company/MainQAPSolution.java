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
    int[] currentFacilityPermutation; // Facility values start at 1; 0 is used for special case // TODO: make -1 special case and start at 0
    int now = -1;

    int min = 0;
    int max = 0;
    int[] bestPermutation;
    int[] worstPermutation;

    long totalCostAllPermutations;
    long totalSquaredCostAllPermutations;

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
        writeHashMapDataToFile(filename);
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
        handleCost(permutation, permutationCost);

        int permutationCount = countResults(getResults());
        log(permutationCount + " ** " + getPermutationString(permutation) + " : " + permutationCost + "**", 2);
    }

    public void handleCost(int[] permutation, int permutationCost) {
        if (min == 0 || min >= permutationCost) {
            min = permutationCost;
            bestPermutation = permutation;
        } else if (max < permutationCost) {
            max = permutationCost;
            worstPermutation = permutation;
        }

        results.computeIfAbsent(permutationCost, k -> new ArrayList<int[]>());
        results.get(permutationCost).add(permutation);
    }

    public int calculateCost(int[] permutation) {
        int cost = 0;
        for (int i = 0; i < permutation.length; i++) {
            int facility = permutation[i] - 1;
            for (int j = 0; j < permutation.length; j++) {
                int receivingFacility = permutation[j] - 1;
                int distance = distanceMatrix[i][j];
                cost = cost + (distance * flowMatrix[facility][receivingFacility]);
            }
        }
        return cost;
    }

    public String getPermutationString(int[] permutation) {
        String returnString = "";
        if (permutation == null) {
            permutation = new int[1];
        }
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
        log("Total cost is :" + totalCostAllPermutations, 1);
        log("Sum of the squared costs is :" + totalSquaredCostAllPermutations, 1);
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

        flowMatrix = getMatrix(inputs[1]);
        distanceMatrix = getMatrix(inputs[0]);

        sc.close();
    }

    public int[][] getMatrix(int[] list) {
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

    private void writeHashMapDataToFile(String filename) throws IOException {

        FileWriter fileWriter = new FileWriter(filename);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        double seconds = (double) estimatedTime / 1000000000.0;
        bufferedWriter.write("Time to calculate in seconds: " + seconds);
        bufferedWriter.newLine();

        bufferedWriter.write("Min cost : " + min + " ::::: Permutation is " + getPermutationString(bestPermutation));
        bufferedWriter.newLine();
        bufferedWriter.write("Max cost : " + max + " ::::: Permutation is " + getPermutationString(worstPermutation));
        bufferedWriter.newLine();
        bufferedWriter.write("Total cost is :" + totalCostAllPermutations);
        bufferedWriter.newLine();
        bufferedWriter.write("Sum of the squared costs is :" + totalSquaredCostAllPermutations);
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

    private int countResults(HashMap<Integer, ArrayList<int[]>> results) {
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