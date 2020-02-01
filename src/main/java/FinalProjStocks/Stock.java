package FinalProjStocks;

import Feedfoward.NNMath;
import Feedfoward.NNObj;

public class Stock extends NNObj {
    public static double highest = 0;
    public static double lowest = 1000000000;
    public static double highestVol = 0;
    public static double lowestVol = 1000000000;
    public double previousDayClose;
    public String classification;
    public double predictedPrice;
    String date;
    double close;
    double volume;
    double open;
    double high;
    double low;
    double volume1;
    double close1 = 1000000001;
    double open1 = 1000000001;
    double high1 = 1000000001;
    double low1 = 1000000001;
    double closePrediction = 1000000001;
    double closePrediction1 = 1000000001;
    double previousDayClose1 = 1000000001;
    double lowestPrior;
    double highestPrior;
    double lowestPriorVol;
    double highestPriorVol;
    double factor = 1;
    double minThreshClass = 0.0;
    double maxThreshClass = 3.5;

    public Stock() {
    }

    public Stock(String date, double close, int volume, double open, double high, double low) {
        this.date = date;
        this.volume = volume;
        this.close = close;
        this.open = open;
        this.high = high;
        this.low = low;

    }

    public void normalizeUpdatedVals() {
        open1 = NNMath.calcSimpleLineVal(lowest, highest, open);
        close1 = NNMath.calcSimpleLineVal(lowest, highest, close);
        high1 = NNMath.calcSimpleLineVal(lowest, highest, high);
        low1 = NNMath.calcSimpleLineVal(lowest, highest, low);
        closePrediction1 = NNMath.calcSimpleLineVal(lowest, highest, closePrediction);
        previousDayClose1 = NNMath.calcSimpleLineVal(lowest, highest, previousDayClose);
        volume1 = NNMath.calcSimpleLineVal(lowestVol / 1000000, highestVol / 1000000, volume / 1000000);

        updatedVals();
    }

    public void setupClass() {
        //(-1*(B9-B10)/B9)*100
        double temp = (-1 * (close - previousDayClose) / close) * 100;
        if (Math.abs(temp) > minThreshClass && Math.abs(temp) < maxThreshClass) {
            classification = "H";
        } else if (temp > 0) {
            classification = "B";
        } else {
            classification = "S";
        }
    }

    public void updatedVals() {
        if (close1 <= 1000000000) {
            this.dVals = new double[]{(open1 / factor), (high1 / factor), (low1 / factor), (previousDayClose1 / factor), (volume1 / factor)};
            this.dVals_targets = new double[]{closePrediction1 / factor};

//            setValues = new SetValues(dVals, dVals_targets);
        }

        if (high > highest) {
            highest = high;
            highestPrior = highest;
        } else {
            highestPrior = highest;
        }
        if (low < lowest) {
            lowest = low;
            lowestPrior = lowest;
        } else {
            lowestPrior = lowest;
        }


        if (volume > highestVol) {
            highestVol = volume;
            highestPriorVol = highestVol;
        } else {
            highestPriorVol = highestVol;
        }
        if (volume < lowestVol) {
            lowestVol = volume;
            lowestPriorVol = lowestVol;
        } else {
            lowestPriorVol = lowestVol;
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        this.id++;
        desc = id + ": date";
    }

    public void updateDesc() {
        desc = id + ": date";
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }
}
