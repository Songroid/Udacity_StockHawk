package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Song on 3/6/16.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static String LOG_TAG = WidgetDataProvider.class.getSimpleName();

    private Context mContext;
    private List<StockWidgetItem> mCollection = new ArrayList<>();

    public WidgetDataProvider(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_collection_item);
        view.setTextViewText(R.id.stock_symbol, mCollection.get(position).getSymbol());
        view.setTextViewText(R.id.change, mCollection.get(position).getPercentChange());
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        mCollection.clear();

        final long token = Binder.clearCallingIdentity();
        try {
            Cursor c = mContext.getContentResolver().query(
                    QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{QuoteColumns.SYMBOL, QuoteColumns.CHANGE,
                            QuoteColumns.PERCENT_CHANGE, QuoteColumns._ID},
                    null,
                    null,
                    QuoteColumns.SYMBOL + " ASC, " + QuoteColumns._ID + " DESC"
            );
            int symbolIndex = c.getColumnIndex(QuoteColumns.SYMBOL);
            int changeIndex = c.getColumnIndex(QuoteColumns.CHANGE);
            int changePercentIndex = c.getColumnIndex(QuoteColumns.PERCENT_CHANGE);

            if (c.getCount() != 0) {
                String symbol = "";
                while (c.moveToNext()) {
                    if (!symbol.equals(c.getString(symbolIndex))) {
                        // Gets the value from the column.
                        symbol = c.getString(symbolIndex);
                        StockWidgetItem stock = new StockWidgetItem(symbol,
                                c.getString(changeIndex),
                                c.getString(changePercentIndex));
                        mCollection.add(stock);
                        Log.d(LOG_TAG, "c.moveToNext() " + stock);
                    }
                }
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }
}
