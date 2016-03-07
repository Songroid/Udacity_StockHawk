package com.sam_chordas.android.stockhawk.widget;

/**
 * Created by Song on 3/6/16.
 */
public class StockWidgetItem {
    private String symbol;
    private String change;
    private String percentChange;
    private String id;

    public StockWidgetItem(String symbol, String change, String percentChange, String id) {
        this.symbol = symbol;
        this.change = change;
        this.percentChange = percentChange;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "StockWidgetItem{" +
                "symbol='" + symbol + '\'' +
                ", change='" + change + '\'' +
                ", percentChange='" + percentChange + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
