package models.GeneticAlgorithm.company;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Apr 20, 2023: This code is over 5 years old and needs to be heavily refactored. Lots of novice mistakes and issues
 */
public class MainQAPSolution {

    static HashMap<Integer, Integer> results = new HashMap<>();
    int[] val;
    int now = -1;
    int V = 4;
    long count = 0;
    int extraCount = 0;
    int countEqualOrImprove = 0;
    int[][] flowMatrix;
    int[][] distanceMatrix;
    String file = "had20.txt";
    int min = 0;
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
    Boolean randomized = true; //If true, use a number of random permutations instead of running through
    int randomCount = 500;
    int improvementAttempts = 50000;
    Boolean runHelperAlgorirthm = true;
    HashMap<String, int[]> randomResults = new HashMap<>();
    HashMap<String, int[]> improvingResults = new HashMap<>();

    public void main() throws IOException {

        startTime = System.nanoTime();
        readInData();

        createAndProcessPermutations();

        if (runHelperAlgorirthm) {
            myAlgorithm();
        }

        estimatedTime = System.nanoTime() - startTime;

        consoleReport();
        processHashMapData();
    }


    public void myAlgorithm() {
        //take best from 1 million
        ArrayList<int[]> cloneBestPermutation = (ArrayList<int[]>) bestPermutations.clone();
        for (int[] potentialSolution : cloneBestPermutation) {
            myAlgorithmHelper(potentialSolution, improvementAttempts);
        }
    }

    private int myAlgorithmHelper(int[] potentialSolution, int improvementAttempts) {
        int newCost = min;
        int limit = 2;
        for (int i = 0; i < improvementAttempts; i++) {
            if (limit >= 10) {
                limit = 5;
            }
            boolean check = true;
            int[] potenialPermutation = null;
            while (check) {
                potenialPermutation = shuffleArray(potentialSolution, limit);
                check = checkArrayAgainstKnown(potenialPermutation);
            }
            if (!checkArrayAgainstKnown(potenialPermutation)) {
                newCost = calculateCost(potenialPermutation);
                extraCount++;
                System.out.println("#" + extraCount);
                standardDeviation = (long) Math.sqrt((totalSquaredCostAllPermutations -
                        (totalCostAllPermutations * totalCostAllPermutations / count)) / count);
                if (newCost <= min || newCost - (standardDeviation / 10) < min) {
                    countEqualOrImprove++;
                    if (newCost <= min) {
                        improvingResults.put(getArrayString(potenialPermutation), potenialPermutation);
                    }
                    if (!randomResults.containsKey(getArrayString(potenialPermutation))) {
                        handleCost(potenialPermutation, newCost);
                    }
                    newCost = myAlgorithmHelper(potenialPermutation, improvementAttempts);

                    if (limit > 1) {
                        limit--;
                    }
                } else {
                    limit++;
                }
            }
        }
        return newCost;
    }


    public void createAndProcessPermutations() {
        int multiplier = 0;
        if (randomized) {
            multiplier = 1;
        }
        val = new int[V + 1];
        for (int i = 0; i <= V; i++)
            val[i] = i * multiplier;
        if (randomized) {
            randomPermutationRunner();
        } else {
            procedurallyGenPermutations(0);
        }
    }

    public void randomPermutationRunner() {
        while (randomCount > 0) {
            generateValidRandomPermutation();
            handlePermutation();
            randomCount--;
        }
    }

    public void generateValidRandomPermutation() {
        String permutationKey = "";
        Boolean continueWorking = false;
        while (!continueWorking) {
            val = shuffleArray(val);
            permutationKey = getArrayString(val);
            continueWorking = !randomResults.containsKey(val);
        }
        randomResults.put(permutationKey, val);
    }

    private String getArrayString(int[] array) {
        String returnString = "";
        for (int i = 0; i < array.length; i++) {
            returnString = returnString + array[i];
            if (i != array.length - 1) {
                returnString = returnString + ".";
            }
        }
        return returnString;
    }

    public int[] shuffleArray(int[] array) {
        return shuffleArray(array, array.length);
    }

    public int[] shuffleArray(int[] array, int limit) {
        Random random = new Random();
        random.nextInt();
        for (int i = 1; i < limit; i++) {
            int index = i;

            if (limit != array.length) {
                index = 1 + random.nextInt(array.length - 2);
            }
            int swapInt = index;
            while (swapInt == index) {
                swapInt = 1 + random.nextInt(array.length - 2);//index
            }
            int tempInt = array[index];//value to be replaced
            array[index] = array[swapInt];
            array[swapInt] = tempInt;
        }
        return array;
    }

    private boolean checkArrayAgainstKnown(int[] array) {
        if (randomResults.containsKey(getArrayString(array)) || improvingResults.containsKey(getArrayString(array))) {
            return true;
        }
        return false;
    }

    public void consoleReport() {
        if (count == 0) {
            count++;
        }
        double seconds = (double) estimatedTime / 1000000000.0;
        System.out.println("Time to calculate in seconds: " + seconds);
        averageCost = totalCostAllPermutations / count;
        standardDeviation = (long) Math.sqrt((totalSquaredCostAllPermutations -
                (totalCostAllPermutations * totalCostAllPermutations / count)) / count);
        System.out.println("Min cost : " + min + " ::::: Permutation is " + getPermutation(bestPermutation));
        System.out.println("Max cost : " + max + " ::::: Permutation is " + getPermutation(worstPermutation));
        System.out.println("Total cost is :" + totalCostAllPermutations);
        System.out.println("Sum of the squared costs is :" + totalSquaredCostAllPermutations);
        System.out.println("Average cost is: " + averageCost);
        System.out.println("The standard deviation is: " + standardDeviation);
        System.out.println("Total permutations ran: " + count);
        System.out.println("" + getBestPermutationsList());
    }

    public void procedurallyGenPermutations(int k) {
        now++;
        val[k] = now;
        if (now == V) handlePermutation();
        for (int i = 1; i <= V; i++)
            if (val[i] == 0) procedurallyGenPermutations(i);
        now--;
        val[k] = 0;
    }


    public void handlePermutation() {
        count++;
        int[] permutation = new int[V];

        System.out.print(count + " ** ");
        for (int i = 1; i <= V; i++) {
            permutation[i - 1] = val[i];
            //calc cost and store
            System.out.print(val[i] + " ");
        }

        int permutationCost = calculateCost(permutation);
        handleCost(permutation, permutationCost);

        System.out.print(" " + permutationCost);
        System.out.println("**");
        processMetrics(permutationCost);
    }

    public void handleCost(int[] permutation, int permutationCost) {
        if (min == 0 || min > permutationCost) {
            min = permutationCost;
            bestPermutation = permutation;
            bestPermutations = new ArrayList<>();
            bestPermutations.add(permutation);
        } else if (permutationCost == min) {
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
        totalSquaredCostAllPermutations = totalSquaredCostAllPermutations + (cost * cost);
    }

    public String getPermutation(int[] permutation) {
        String returnString = "";
        if (permutation == null) {
            permutation = new int[1];
        }
        for (int aPermutation : permutation) {
            returnString = returnString + " " + aPermutation;
        }
        return returnString;
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

    public void readInData() throws FileNotFoundException {
        Scanner sc = new Scanner(new File(this.file)); // get from project root for now
        int size = sc.nextInt();
        V = size;
        int[][] inputs = new int[2][V * V];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < V * V; j++) {
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
        bufferedWriter.write("Min cost : " + min + " ::::: Permutation is " + getPermutation(bestPermutation));
        bufferedWriter.newLine();
        bufferedWriter.write("Max cost : " + max + " ::::: Permutation is " + getPermutation(worstPermutation));
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
        bufferedWriter.write(getBestPermutationsList());
        for (Map.Entry<Integer, Integer> entry : results.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println("Result Value: " + key + " ... Count: " + value);
            String outputString = "" + key + ", " + value;
            bufferedWriter.newLine();
            bufferedWriter.write(outputString);
        }
        bufferedWriter.newLine();
        bufferedWriter.write("Number of improvements: " + countEqualOrImprove);
        bufferedWriter.newLine();
        bufferedWriter.write("Number of additional permutations attempted: " + extraCount);
        bufferedWriter.close();
    }

    public String getBestPermutationsList() {
        String bestPermutationsString = "Best Permutations are: ";
        for (int i = 0; i < bestPermutations.size(); i++) {
            String perm = "\n ";
            for (int j = 0; j < bestPermutations.get(i).length; j++) {
                perm = perm + "." + bestPermutations.get(i)[j];
            }
            bestPermutationsString = bestPermutationsString + perm;
        }

        return bestPermutationsString;
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