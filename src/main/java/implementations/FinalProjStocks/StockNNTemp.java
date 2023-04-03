package implementations.FinalProjStocks;

import algorithms.NearestNeighbor.ObjectNN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

// TODO: this was old k-nearest neighbor implementation
// Seems like I was doing some odd comparison between open and close prices only?
public class StockNNTemp {

    //dataset
    static double[] yVals = new double[]{10.1, 10, 8.3, 9.0, 8.9, 8.1, 8.3, 7.9, 7.9, 8.1, 7.8, 7.5, 8.1, 8.3, 8.4, 8.9};
    static double[] xVals = new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    static double[] xVals1;
    static double[] yVals1;

    static String[] classPlaceholders = new String[]{"b", "s", "s", "h", "s", "h", "b", "b", "b", "s", "s", "h", "s", "h", "b", "b"};
    static ArrayList<ObjectNN> nearestNeighbors = new ArrayList<>();
    static ArrayList<ObjectNN> neighbors = new ArrayList<>();
    static ArrayList<String> classes2 = new ArrayList<>();

    static int k = 5;
    static ObjectNN unknownObject;
    static ObjectNN[] unknownObjects;

    public static void main(String[] args) throws IOException {
        StocksMain.parseJSON();
        Stock[] newStocks = Arrays.copyOf(StocksMain.jsonToStockArray, StocksMain.jsonToStockArray.length - 1);
        newStocks = StocksMain.reverseArray(newStocks);
        int count = 0;
        yVals = new double[newStocks.length];
        xVals = new double[newStocks.length];
        yVals1 = new double[newStocks.length];
        xVals1 = new double[newStocks.length];
        classPlaceholders = new String[newStocks.length];
        for (Stock stock : newStocks) {
            if (count + 1 < newStocks.length - 1) {
                stock.previousDayClose = newStocks[count + 1].getClose();
            }
            xVals[count] = count;
            yVals[count] = stock.getClose();
            xVals1[count] = count - 0.5;
            yVals1[count] = stock.getOpen();
            stock.setupClass();
            classPlaceholders[count] = stock.classification;
            count++;
        }

        for (int i = 0; i < xVals.length; i++) {
            neighbors.add(new ObjectNN(xVals[i], yVals[i], classPlaceholders[i]));
        }

        unknownObjects = new ObjectNN[newStocks.length];
        for (int i = 0; i < xVals1.length; i++) {
            unknownObjects[i] = new ObjectNN(xVals1[i], yVals1[i], "unknown");
        }

        unknownObjects = reverseArray(unknownObjects);
        unknownObjects = Arrays.copyOf(unknownObjects, unknownObjects.length - 1);
        unknownObjects = reverseArray(unknownObjects);

        for (ObjectNN unknownObject : unknownObjects) {
            updateDistances(neighbors, unknownObject);
//            Collections.sort(neighbors);

            //Grab closest ones
            nearestNeighbors = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                nearestNeighbors.add(neighbors.get(i));
            }


            //figure out count of unique classifiers and use one with most
            int counter = 0;
            for (ObjectNN objectNN : nearestNeighbors) {
                classes2.add(counter, (objectNN.classification));
                counter++;
            }

            HashSet<String> conversionSet = new HashSet<>(classes2);
            classes2 = new ArrayList<>(conversionSet);

            int[] classCount = new int[classes2.size()];

            for (int i = 0; i < classes2.size(); i++) {
                int count1 = 0;
                for (ObjectNN objectNN : nearestNeighbors) {
                    if (objectNN.classification.equals(classes2.get(i))) {
                        count1++;
                    }
                }
                classCount[i] = count1;
            }

            int finalIndex = 0;
            for (int i = 1; i < classCount.length; i++) {
                if (classCount[i] > classCount[finalIndex]) {
                    finalIndex = i;
                }
            }

            unknownObject.classification = classes2.get(finalIndex);

            System.out.println("The class of the unknown object is: " + unknownObject.classification);
            System.out.println("xVAl " + unknownObject.vector.x + "yVAl " + unknownObject.vector.y);

        }

    }

    public static ObjectNN[] reverseArray(ObjectNN[] stocks) {
        ObjectNN[] temp = new ObjectNN[stocks.length];

        int counter = 0;
        for (int i = stocks.length - 1; i >= 0; i--) {
            temp[i] = stocks[counter];
            counter++;
        }
        return temp;
    }

    public static void updateDistances(ArrayList<ObjectNN> neighbors, ObjectNN unknownObject) {
        for (ObjectNN objectNN : neighbors) {
            objectNN.calcDistance(unknownObject);
        }
    }

}
