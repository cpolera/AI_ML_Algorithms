package models.GeneticAlgorithm.company;

import java.io.*;
import java.util.*;

import static models.GeneticAlgorithm.company.QAPUtility.shuffleArray;

/**
 * Apr 20, 2023: This code is over 5 years old and needs to be heavily refactored. Lots of novice mistakes and issues
 */
public class MainQAPSolution {

    int[][] flowMatrix;
    int[][] distanceMatrix;

    static HashMap<Integer, Integer> results = new HashMap<>();
    int[] val;
    int now = -1;
    int locationCount = 4;
    long count = 0;

    String file; // Put at root of project for now //had20.txt is all facilities go to all other facilities
    static int min = 0;
    int[] bestPermutation;
    int[] worstPermutation;
    ArrayList<int[]> bestPermutations = new ArrayList<>();
    long totalCostAllPermutations;
    long totalSquaredCostAllPermutations;
    float averageCost;
    int max = 0;
    float standardDeviation;
    long startTime;
    long estimatedTime;

    public void main() throws IOException {
        startTime = System.nanoTime();
        log("Starting evaluation...", 1);

        readInData(this.file);
        createAndProcessPermutations();

        estimatedTime = System.nanoTime() - startTime;
        log("Completed evaluation.", 1);

        consoleReport();
        processHashMapData();
    }

    private void createAndProcessPermutations() {
        val = new int[locationCount + 1];

        for (int i = 0; i <= locationCount; i++){
            val[i] = i * 0;
        }

        procedurallyGenPermutations(0);
    }

    // TODO: move to class to build these permutations
    public void procedurallyGenPermutations(int k) {
        now++;
        val[k] = now;
        if (now == locationCount) handlePermutation();
        for (int i = 1; i <= locationCount; i++)
            if (val[i] == 0) procedurallyGenPermutations(i);
        now--;
        val[k] = 0;
    }

    /**
     * Calculate's the cost of each permutation
     */
    public void handlePermutation() {
        count++;
        int[] permutation = new int[locationCount];

        String logString = (count + " ** ");
        for (int i = 1; i <= locationCount; i++) {
            permutation[i - 1] = val[i];
            //calc cost and store
            logString += val[i] + " ";
        }

        int permutationCost = calculateCost(permutation);
        handleCost(permutation, permutationCost);

        log(logString + " " + permutationCost + "**", 2);
        processMetrics(permutationCost);
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
        if (count == 0) {
            count++;
        }
        double seconds = (double) estimatedTime / 1000000000.0;
        log("Time to calculate in seconds: " + seconds, 1);
        averageCost = totalCostAllPermutations / count;
        standardDeviation = (long) Math.sqrt((totalSquaredCostAllPermutations -
                (totalCostAllPermutations * totalCostAllPermutations / count)) / count);
        log("Min cost : " + min + " ::::: Permutation is " + getPermutationString(bestPermutation), 1);
        log("Max cost : " + max + " ::::: Permutation is " + getPermutationString(worstPermutation), 1);
        log("Total cost is :" + totalCostAllPermutations, 1);
        log("Sum of the squared costs is :" + totalSquaredCostAllPermutations, 1);
        log("Average cost is: " + averageCost, 1);
        log("The standard deviation is: " + standardDeviation, 1);
        log("Total permutations ran: " + count, 1);
    }

    public void handleCost(int[] permutation, int permutationCost) {
        if (min == 0 || min >= permutationCost) { //
            min = permutationCost;
            bestPermutation = permutation;
            bestPermutations = new ArrayList<>();
            bestPermutations.add(permutation);
        } else if (max < permutationCost) {
            max = permutationCost;
            worstPermutation = permutation;
        }

        if (results.get(permutationCost) != null) {
            int priorVal = results.get(permutationCost);
            results.replace(permutationCost, priorVal + 1);
        } else {
            results.put(permutationCost, 1);
        }
    }

    public void processMetrics(int cost) {
        totalCostAllPermutations = totalCostAllPermutations + cost;
        totalSquaredCostAllPermutations = totalSquaredCostAllPermutations + ((long) cost * cost);
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
        locationCount = size;
        int[][] inputs = new int[2][locationCount * locationCount];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < locationCount * locationCount; j++) {
                inputs[i][j] = sc.nextInt();
            }
        }

        flowMatrix = getMatrix(inputs[1]);
        distanceMatrix = getMatrix(inputs[0]);

        sc.close();
    }

    public void processHashMapData() throws IOException {

        String FILENAME = "results.txt";
        FileWriter fileWriter = new FileWriter(FILENAME);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        double seconds = (double) estimatedTime / 1000000000.0;
        bufferedWriter.write("Time to calculate in seconds: " + seconds);
        bufferedWriter.newLine();
        averageCost = totalCostAllPermutations / count;
        standardDeviation = (long) Math.sqrt((totalSquaredCostAllPermutations -
                (totalCostAllPermutations * totalCostAllPermutations / count)) / count);
        bufferedWriter.write("Min cost : " + min + " ::::: Permutation is " + getPermutationString(bestPermutation));
        bufferedWriter.newLine();
        bufferedWriter.write("Max cost : " + max + " ::::: Permutation is " + getPermutationString(worstPermutation));
        bufferedWriter.newLine();
        bufferedWriter.write("Total cost is :" + totalCostAllPermutations);
        bufferedWriter.newLine();
        bufferedWriter.write("Sum of the squared costs is :" + totalSquaredCostAllPermutations);
        bufferedWriter.newLine();
        bufferedWriter.write("Average cost is: " + averageCost);
        bufferedWriter.newLine();
        bufferedWriter.write("The standard deviation is: " + standardDeviation);
        bufferedWriter.newLine();
        bufferedWriter.write("Total permutations ran: " + count);
        bufferedWriter.newLine();
        for (Map.Entry<Integer, Integer> entry : results.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
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