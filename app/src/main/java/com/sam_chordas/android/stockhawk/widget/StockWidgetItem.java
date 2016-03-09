package com.sam_chordas.android.stockhawk.widget;

/**
 * Created by Song on 3/6/16.
 */
public class StockWidgetItem {
    private String symbol;
    private String change;
    private String percentChange;
    private int isUp;

    public StockWidgetItem(String symbol, String change, String percentChange, int isUp) {
        this.symbol = symbol;
        this.change = change;
        this.percentChange = percentChange;
        this.isUp = isUp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(String percentChange) {
        this.percentChange = percentChange;
    }

    public int getIsUp() {
        return isUp;
    }

    public void setIsUp(int isUp) {
        this.isUp = isUp;
    }

    @Override
    public String toString() {
        return "StockWidgetItem{" +
                "symbol='" + symbol + '\'' +
                ", change='" + change + '\'' +
                ", percentChange='" + percentChange + '\'' +
                ", isUp=" + isUp +
                '}';
    }
}
