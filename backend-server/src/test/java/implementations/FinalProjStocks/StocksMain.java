package implementations.FinalProjStocks;

import com.implementations.FinalProjStocks.Stock;
import com.implementations.FinalProjStocks.StockGroup;
import io.github.cdimascio.dotenv.Dotenv;
import com.implementations.models.FeedForward.NNMath;
import com.implementations.models.FeedForward.components.Network;
import com.implementations.models.FeedForward.components.NetworkTester;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.implementations.FinalProjStocks.utils.TradesUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class StocksMain {
    public static Stock[] stockDailyData;
    static int sampleExtractionSize = 10;
    Stock[] jsonToStockArray_cgc;
    Stock[] jsonToStockArray_msft;
    Stock[] testingGroup;
    Stock[] trainingGroup;
    StockGroup[] testingStockGroups;
    StockGroup[] trainingStockGroups;
    StockGroup[] stockGroups_CRON;
    StockGroup[] stockGroups_CGC;
    int periodSize_Input = 10;
    int outputDay = 1;
    int arrayLengthIfOutputDayNext = -1;

    public StocksMain() {
    }

    /**
     * main function is way too heavy. Need to refactor and abstract what I can
     *
     * This is really rough
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Dotenv.configure().systemProperties().load();

        StocksMain stocksMain = new StocksMain();
        stockDailyData = stocksMain.parseJSON("CRON.json");
        //jsonToStockArray_cgc = objectMapper.readValue(new File("src/FinalProjStocks/CGC.json"), Stock[].class);
        //jsonToStockArray_msft = objectMapper.readValue(new File("src/FinalProjStocks/MSFT.json"), Stock[].class);
        stocksMain.prepareCRON(1);

        Stock.highest = 0;
        Stock.lowest = 100000;

        //stocksMain.prepareCGC();

        //shuffle to randomize order
        //Collections.shuffle(Arrays.asList(stocksMain.stockGroups_CRON));
        Collections.shuffle(Arrays.asList(stocksMain.stockDailyData));
        //Collections.shuffle(Arrays.asList(stocksMain.jsonToStockArray_cgc));
        stocksMain.createStockTesting_TrainingLists();
        //stocksMain.createStockGroupTesting_TrainingLists();

        Network network = new Network(stocksMain.trainingGroup, stocksMain.testingGroup);

        //Network network = new Network(stocksMain.trainingStockGroups, stocksMain.testingStockGroups);
//        network.trainingCountPerCycle = 1000;
        network.hiddenNeuronCount = 10;
        network.hiddenNeuronLayersCount = 1;
        network.desiredError = 0.05;
        network.TSSE = 0.02;
        network.learningRate = 0.25;
        network.acceptablePassRate = 0.6;
        network.biasVal = 1;
        network.maxTrainingCycles = 1;
        stocksMain.periodSize_Input = 5;//TEST USING MOST RECENT INSTEAD OF FULL YEAR
//        network.setupNetwork();//TODO REMOVED METHOD

//        Stock[] stocks = new Stock[1];
//        Stock stock = new Stock("date", 10.8, 100800, 10, 13, 9);
//        stock.updatedVals();
//        stock.normalizeUpdatedVals();
//        stocks[0] = stock;
//        //network.predictOutput(stocks);

        stocksMain.prepareFinalTestArrays();
        stockDailyData = stocksMain.reverseArray(stockDailyData);
        //stocksMain.prepareFinalTestArrays();
        stocksMain.createStockTesting_TrainingLists();
        System.out.println("PREDICTING OUTCOME==========================================================");
        NetworkTester networkTester = new NetworkTester(network);
        networkTester.testNetwork(stocksMain.testingGroup);
        System.currentTimeMillis();

        stocksMain.consoleReport(stocksMain.testingGroup);
    }

    public static Stock[] reverseArray(Stock[] stocks) {
        Stock[] temp = new Stock[stocks.length];

        int counter = 0;
        for (int i = stocks.length - 1; i >= 0; i--) {
            temp[i] = stocks[counter];
            counter++;
        }
        return temp;
    }

    public static Stock[] parseJSON(String filename) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Stock[] stocks = objectMapper.readValue(
                new File("src/test/java/implementations/FinalProjStocks/data/" + filename),
                Stock[].class
        );

        return stocks;
    }

    public void prepareFinalTestArrays() throws IOException {
        stockDailyData = parseJSON("CRON.json");
        prepareCRON(stockDailyData.length - 31);

        //jsonToStockArray = updateArray_PredictNextDay_UsingCurrentClose(jsonToStockArray);
    }

    private Stock[] updateArray_PredictNextDay_UsingCurrentClose(Stock[] stocks) {
        stocks = reverseArray(stocks);

        for (int i = 0; i < stocks.length - 2; i++) {
            Stock stock = stocks[i];
            double currentDayVal = stocks[i + 1].getClose();
            stock.previousDayClose = stocks[i].getClose();
            stock.closePrediction = currentDayVal;
            stock.normalizeUpdatedVals();
        }
        return Arrays.copyOf(stocks, stocks.length - 1);
    }

    private void createStockTesting_TrainingLists() {
        testingGroup = new Stock[sampleExtractionSize];
        int counter = 0;
        for (int i = StocksMain.sampleExtractionSize; i >= 1; i--) {
            testingGroup[counter] = stockDailyData[i];
            counter++;
        }
        trainingGroup = Arrays.copyOf(stockDailyData, stockDailyData.length - 15);
    }

    private void createStockGroupTesting_TrainingLists() {
        testingStockGroups = new StockGroup[sampleExtractionSize];
        int counter = 0;
        for (int i = StocksMain.sampleExtractionSize; i >= 1; i--) {
            testingStockGroups[counter] = stockGroups_CRON[i];
            counter++;
        }
        trainingStockGroups = Arrays.copyOf(stockGroups_CRON, stockGroups_CRON.length - 10);
    }

    private void prepareCGC() {
        for (Stock stock : jsonToStockArray_cgc) {
            stock.updatedVals();
            stock.updateDesc();
        }
        for (Stock stock : jsonToStockArray_cgc) {
            stock.normalizeUpdatedVals();
        }
        for (int i = 0; i < jsonToStockArray_cgc.length - 1; i++) {
            Stock stock = jsonToStockArray_cgc[i];
            double nextDayVal = jsonToStockArray_cgc[i + 1].getClose();
            stock.previousDayClose = nextDayVal;
            stock.normalizeUpdatedVals();
        }
        jsonToStockArray_cgc = Arrays.copyOf(jsonToStockArray_cgc, jsonToStockArray_cgc.length - 1);
    }

    private void prepareCRON(int dayReduction) {
        for (Stock stock : stockDailyData) {
            stock.updatedVals();
            stock.updateDesc();
        }
        for (int i = 1; i < stockDailyData.length + arrayLengthIfOutputDayNext; i++) {
            Stock stock = stockDailyData[i];
            double nextDayVal = stockDailyData[i + outputDay].getClose();
            stock.previousDayClose = nextDayVal;//SETS PREVIOUS DAY AS NEXT DAY
            stock.closePrediction = stockDailyData[i - 1].getClose();
            stock.normalizeUpdatedVals();
        }
        stockDailyData = Arrays.copyOf(stockDailyData, stockDailyData.length - (dayReduction));
        stockDailyData = reverseArray(stockDailyData);
        stockDailyData = Arrays.copyOf(stockDailyData, stockDailyData.length - 1);

        stockGroups_CRON = createStockGroups(stockDailyData, new int[]{periodSize_Input});
    }

    /**
     * Create groupings of daily data nodes
     *
     * @param stockData
     * @param groupSizes
     * @return
     */
    public StockGroup[] createStockGroups(Stock[] stockData, int[] groupSizes) {
        int size = 0;
        for (int i : groupSizes) {
            size = size + (stockData.length - i);
        }
        StockGroup[] stockGroups = new StockGroup[size];

        for (int i = 0; i < groupSizes.length; i++) {
            int periodSize = groupSizes[i];

            int count = 0;
            int stockCount = 0;


            for (int j = 0; j < stockData.length - periodSize; j++) {
                count = 0;
                if (stockCount > periodSize - 1) {
                    stockCount = stockCount - (periodSize - 1);
                }
                Stock[] tempList = new Stock[periodSize];
                while (count % periodSize != 0 || count == 0) {
                    tempList[count] = stockData[stockCount];
                    stockCount++;
                    count++;
                }
                stockGroups[j] = new StockGroup(tempList);
            }
        }


        return stockGroups;
    }

    private void consoleReport(Stock[] testingGroup){
        int count = 0;
        double avg_Error = 0.0;
        int sugSharesTotal = 10;
        int idealSharesTotal = 10;
        double totalSugPortfolioVal = 85.0;
        double totalIdealPortfolioVal = 85.0;
        double averageShareCostIdeal = totalIdealPortfolioVal / idealSharesTotal;
        double averageShareCostSug = totalSugPortfolioVal / sugSharesTotal;
        double sugCash = 200;
        double idealCash = 200;
        double startingCapital = 285;
        String prevState = "S";
        String prevStateIdeal = "S";
        System.out.println("Starting capital: " + startingCapital
                + " \tStarting portfolio value: " + totalIdealPortfolioVal
        );

        System.out.printf("%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s\n",
                "Date"
                //" \t Current Close: "  +
                , "Pred. Price & Act."
                //+ " \t Dif Act. Next And Pred" + String.format( "%.3f", difResult)
                , "Act. C2C % Change"
                , "Pred. C2C % Change"
                , "Error % Next Close"
                //+ " \t Error Overall Day to Day: "
                , "Sug Action"
                , "Correct Action"
                , "Est Shares"
                , "Est Portfolio Val"
                , "Est Cash"
                , "Est Total Capital"
                , "Ideal Shares"
                , "Ideal Port Val "
                , "Ideal Cash"
                , "Ideal Total Capital"
        );
        for (Stock stock : testingGroup) {
            double predictedNextPrice = NNMath.reverseSimpleLine(Stock.lowest, Stock.highest, stock.predictedPrice);
            double difResult = (predictedNextPrice - stock.closePrediction);
            double predictedPriceChange = (predictedNextPrice - stock.getClose()) / stock.getClose();
            double nominalPriceChange = stock.closePrediction - stock.getClose();
            double percentChangeActual = nominalPriceChange / stock.getClose();
            double priceChangeError = difResult - nominalPriceChange;
            double priceChangePercentError = priceChangeError / nominalPriceChange;
            double error = percentChangeActual - predictedPriceChange;
            error = error * 100;
            avg_Error = avg_Error + Math.abs(error);
            count++;
            String suggestion = "";
            String idealSuggestion = "";

            double tempSug = (averageShareCostSug + nominalPriceChange);
            double tempIdeal = (averageShareCostIdeal + nominalPriceChange);

            if (predictedPriceChange > 0.02) {
                suggestion = "BUY";
                int purchaseQ = Math.max(sugSharesTotal / 4, 4);
                sugSharesTotal += purchaseQ;
                sugCash -= averageShareCostSug * purchaseQ;

            }
            if (predictedPriceChange < -0.02) {
                suggestion = "SELL";
                int sellQ = sugSharesTotal / 2;
                sugSharesTotal -= sellQ;
                sugCash += averageShareCostSug * sellQ;
            }
            if (Math.abs(predictedPriceChange) < 0.02) {
                suggestion = "HOLD";
            }

            if (percentChangeActual > 0.005) {
                idealSuggestion = "BUY";
                int purchaseQ = Math.max(idealSharesTotal / 4, 4);
                idealSharesTotal = idealSharesTotal + (purchaseQ);
                idealCash -= averageShareCostIdeal * (purchaseQ);
                prevStateIdeal = "B";
                prevState = "B";
            }
            if (percentChangeActual < -0.005) {
                idealSuggestion = "SELL";
                int sellQ = idealSharesTotal / 2;
                idealSharesTotal -= sellQ;
                idealCash += averageShareCostIdeal * sellQ;
                prevStateIdeal = "S";
                prevState = "S";
            }
            if (Math.abs(percentChangeActual) < 0.005) {
                idealSuggestion = "HOLD";
                prevStateIdeal = "B";
                prevState = "H";
            }
            String tempMMSug = TradesUtil.predictTrade(prevState);
            String tempMMIdeal = TradesUtil.predictTrade(prevStateIdeal);

            totalSugPortfolioVal = (averageShareCostSug + nominalPriceChange) * sugSharesTotal;
            totalIdealPortfolioVal = (averageShareCostIdeal + nominalPriceChange) * idealSharesTotal;

            averageShareCostSug = totalSugPortfolioVal / sugSharesTotal;
            averageShareCostIdeal = totalIdealPortfolioVal / idealSharesTotal;

            System.out.printf("%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s%-22s\n",
                    stock.getDate()
                    //,String.format( "%.2f", stock.close) +
                    , String.format("%.3f", predictedNextPrice) + "---" + String.format("%.2f", stock.closePrediction)
                    //,Dif Act. Next And Pred: " + String.format( "%.3f", difResult)
                    , String.format("%.3f", percentChangeActual * 100)
                    , String.format("%.3f", predictedPriceChange * 100) + "%"
                    , String.format("%.3f", error) + "%"
                    //,String.format( "%.3f", priceChangePercentError)+ "%"
                    , suggestion + " (" + tempMMSug + ")"
                    , idealSuggestion + " (" + tempMMIdeal + ") "
                    , sugSharesTotal
                    , String.format("%.2f", totalSugPortfolioVal)
                    , String.format("%.2f", sugCash)
                    , String.format("%.2f", totalSugPortfolioVal + sugCash)
                    , idealSharesTotal
                    , String.format("%.2f", totalIdealPortfolioVal)
                    , String.format("%.2f", idealCash)
                    , String.format("%.2f", totalIdealPortfolioVal + idealCash)
            );
        }

        System.out.println("Predicted portfolio value = " + (totalSugPortfolioVal + sugCash));
        System.out.println("Ideal portfolio value = " + (totalIdealPortfolioVal + idealCash));
        System.out.println("Average Error of Predicted Price: " + String.format("%.2f", (avg_Error / count)));
    }
}
