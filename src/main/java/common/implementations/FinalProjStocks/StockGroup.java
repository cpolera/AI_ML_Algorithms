package common.implementations.FinalProjStocks;

import common.implementations.models.FeedForward.components.NNObj;

public class StockGroup extends NNObj {

    Stock[] stocks;
    double[] perChange;

    public StockGroup(Stock[] stocks) {
        this.stocks = stocks;

        perChange = new double[this.stocks.length - 2];

        for (int i = 0; i < this.stocks.length - 2; i++) {
            perChange[i] = getPercentChange(i);
        }
        dVals = perChange;

        double perCh = getPercentChange(this.stocks.length - 2);
        if (perCh > 0) {
            dVals_targets = new double[]{perCh, 0};
        } else {
            dVals_targets = new double[]{0, perCh};
        }

        dVals_targets = new double[]{perCh};
    }

    public static Stock[] genStocks() {
        Stock[] stocks = new Stock[50];
        int count = 0;
        int incrementor = 10;
        int price = 42;

        for (int i = 0; i < stocks.length; i++) {
            stocks[i] = new Stock();
            stocks[i].setClose(price + 1.0);
            count++;
            stocks[i].updatedVals();
            stocks[i].updateDesc();
            if (count <= 5) {
                price = price + 2;
            }
            if (count > 5) {
                price = price + 10;
            }
            if (count == 10) {
                count = 0;
            }
        }

        return stocks;

    }

    public double getPercentChange(int i) {
        try {
            double dif = ((this.stocks[i + 1].close - this.stocks[i].close) / this.stocks[i].close);
            return dif;
        } catch (Exception e) {
            System.out.println("");
        }

        return 0.0;
    }
}
