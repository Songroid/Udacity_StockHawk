package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.util.Log;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

    private static String LOG_TAG = Utils.class.getSimpleName();
    private static String NULL = "null";

      public static boolean showPercent = true;

      public static ArrayList quoteJsonToContentVals(String JSON){
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject = null;
        JSONArray resultsArray = null;
        Log.i(LOG_TAG, "GET FB: " +JSON);
        try{
          jsonObject = new JSONObject(JSON);
          if (jsonObject != null && jsonObject.length() != 0){
            jsonObject = jsonObject.getJSONObject("query");
            int count = Integer.parseInt(jsonObject.getString("count"));
            if (count == 1){
              jsonObject = jsonObject.getJSONObject("results")
                  .getJSONObject("quote");
              batchOperations.add(buildBatchOperation(jsonObject));
            } else{
              resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

              if (resultsArray != null && resultsArray.length() != 0){
                for (int i = 0; i < resultsArray.length(); i++){
                  jsonObject = resultsArray.getJSONObject(i);
                  batchOperations.add(buildBatchOperation(jsonObject));
                }
              }
            }
          }
        } catch (JSONException e){
          Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return batchOperations;
      }

      public static String truncateBidPrice(String bidPrice){
          if (!bidPrice.equals(NULL)) {
              bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
          }
          return bidPrice;
      }

      public static String truncateChange(String change, boolean isPercentChange){
          if (!change.equals(NULL)) {
              String weight = change.substring(0,1);
              String ampersand = "";
              if (isPercentChange){
                  ampersand = change.substring(change.length() - 1, change.length());
                  change = change.substring(0, change.length() - 1);
              }
              change = change.substring(1, change.length());
              double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
              change = String.format("%.2f", round);
              StringBuffer changeBuffer = new StringBuffer(change);
              changeBuffer.insert(0, weight);
              changeBuffer.append(ampersand);
              change = changeBuffer.toString();
          }
          return change;
      }

      public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject){
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
            QuoteProvider.Quotes.CONTENT_URI);
        try {
          Log.d(LOG_TAG, "buildBatchOperation input: " + jsonObject);
          String change = jsonObject.getString("Change");
          builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));

          String bidPrice = truncateBidPrice(jsonObject.getString("Bid"));
          String changePercent = truncateChange(jsonObject.getString("ChangeinPercent"), true);
          String changeNum = truncateChange(change, false);

          if (isStringNull(bidPrice) && isStringNull(changePercent) && isStringNull(changeNum)) {
            return null;
          }

          builder.withValue(QuoteColumns.BIDPRICE, bidPrice);
          builder.withValue(QuoteColumns.PERCENT_CHANGE, changePercent);
          builder.withValue(QuoteColumns.CHANGE, changeNum);
          builder.withValue(QuoteColumns.ISCURRENT, 1);
          if (change.charAt(0) == '-'){
            builder.withValue(QuoteColumns.ISUP, 0);
          }else{
            builder.withValue(QuoteColumns.ISUP, 1);
          }

        } catch (JSONException e){
          e.printStackTrace();
        }
        return builder.build();
      }

    private static boolean isStringNull(String input) {
        return input.equals(NULL);
    }
}
