package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.Constants;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StockDetailActivity extends AppCompatActivity {

    final String TAG = StockDetailActivity.this.getClass().getSimpleName();

    private String symbol;
    private String startDate;
    private String endDate;

    private LineChartView mChart;
    private TextView errorTextHolder;
    private Button retryButton;

    private String[] mLabels;
    private float[] mValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        Intent intent = getIntent();
        if (intent != null) {
            symbol = intent.getStringExtra(Constants.SYMBOL);
        }

        mChart = (LineChartView) findViewById(R.id.linechart);
        errorTextHolder = (TextView) findViewById(R.id.error);
        retryButton = (Button) findViewById(R.id.error_button);

        getDates();
        getPrices(null);

        Toast.makeText(this, R.string.showing_past_month_data, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        if (symbol != null) {
            actionBar.setTitle(symbol.toUpperCase());
        }
    }

    private void getDates() {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        endDate = format.format(date);

        c.add(Calendar.MONTH, -1);
        date = c.getTime();
        startDate = format.format(date);
        Log.d(TAG, "end date and start date: " + endDate + "/" + startDate);
    }

    public void getPrices(View view) {
        StringBuilder urlStringBuilder = new StringBuilder();
        try {
            urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata " +
                    "where symbol = \"" + symbol + "\" and startDate = \"" + startDate + "\" and " +
                    "endDate = \"" + endDate + "\"", "UTF-8"));
            urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                    + "org%2Falltableswithkeys&callback=");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = urlStringBuilder.toString();
        Log.d(TAG, "url is " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                showErrorComponents(true, e.getLocalizedMessage());
                Log.d(TAG, e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String data = response.body().string();
                Log.d(TAG, data);
                if (response.isSuccessful()) {
                    showErrorComponents(false, "");
                    onProcessResponse(data);
                } else {
                    showErrorComponents(true, response.body().string());
                }
            }
        });

    }

    private LineSet setDataset() {
        LineSet dataset = new LineSet(mLabels, mValues);
        int primaryColorDark = getResources().getColor(R.color.material_blue_700);
        int primaryColor = getResources().getColor(R.color.material_blue_500);
        dataset.setColor(primaryColor)
                .setFill(primaryColorDark)
                .setDotsColor(primaryColor)
                .setThickness(4)
                .beginAt(0);
        return dataset;
    }

    private void onProcessResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            if (json.length() != 0) {
                json = json.getJSONObject("query");
                int count = Integer.parseInt(json.getString("count"));
                if (count == 1) {
                    // Highly unlikely happen for a past month data :(
                } else {
                    JSONArray results = json.getJSONObject("results").getJSONArray("quote");
                    mLabels = new String[count];
                    mValues = new float[count];

                    if (results != null && results.length() != 0) {
                        for (int i = 0; i < results.length(); i++) {
                            json = results.getJSONObject(i);
                            String date = json.getString("Date");
                            float closePrice = Float.parseFloat(json.getString("Close"));

                            Log.d(TAG, String.format("Day %s close price is %s", i, closePrice));

                            mLabels[results.length()-1-i] = date;
                            mValues[results.length()-1-i] = closePrice;
                        }
                    }

                    mChart.addData(setDataset());
                    mChart.show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showErrorComponents(final boolean on, final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorTextHolder.setText(msg);
                errorTextHolder.setVisibility(on ? View.VISIBLE : View.GONE);
                retryButton.setVisibility(on ? View.VISIBLE : View.GONE);
            }
        });
    }

}
