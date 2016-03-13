package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.Constants;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.sam_chordas.android.stockhawk.ui.StockDetailActivity;

/**
 * Created by Song on 3/6/16.
 */
public class QuoteWidgetProvider extends AppWidgetProvider {

    private final String TAG = QuoteWidgetProvider.this.getClass().getSimpleName();

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_collection);

        Intent active = new Intent(context, QuoteWidgetProvider.class);
        active.setAction(Constants.ACTION_WIDGET_GO_HOME);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        views.setOnClickPendingIntent(R.id.widget, actionPendingIntent);

        Intent stockIntent = new Intent(context, QuoteWidgetProvider.class);
        stockIntent.setAction(Constants.ACTION_WIDGET_GO_DETAILS);
        PendingIntent stockPendingIntent = PendingIntent.getBroadcast(context, 0, stockIntent, 0);
        views.setPendingIntentTemplate(R.id.widget_list, stockPendingIntent);

        setRemoteAdapter(context, views);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate is called");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constants.ACTION_WIDGET_GO_HOME)) {
            Log.i(TAG, Constants.ACTION_WIDGET_GO_HOME);
            Intent i = new Intent(context, MyStocksActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else if (intent.getAction().equals(Constants.ACTION_WIDGET_GO_DETAILS)) {
            Log.d(TAG, Constants.ACTION_WIDGET_GO_DETAILS);
            String symbol = intent.getStringExtra(Constants.SYMBOL);

            Intent i = new Intent(context, StockDetailActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(Constants.SYMBOL, symbol);
            context.startActivity(i);
        } else {
            super.onReceive(context, intent);
        }
    }

    private static void setRemoteAdapter(Context context, RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list, new Intent(context, QuoteWidgetRemoteViewsService.class));
    }
}
