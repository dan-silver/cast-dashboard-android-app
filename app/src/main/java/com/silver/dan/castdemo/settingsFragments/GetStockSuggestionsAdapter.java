package com.silver.dan.castdemo.settingsFragments;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.StockInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 1/16/17.
 */
public class GetStockSuggestionsAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<StockInfo> resultList = new ArrayList<>();

    public GetStockSuggestionsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public StockInfo getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.stock_auto_complete_dropdown, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.company_name)).setText(getItem(position).getName());
        ((TextView) convertView.findViewById(R.id.stock_ticker)).setText(getItem(position).getTicker());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null) {
                    // A class that queries a web API, parses the data and returns an ArrayList<Style>
//
                    try {
                        resultList = new DownloadShippers().execute(new String[]{constraint.toString()}).get();
                    }
                    catch(Exception e) {
//                        Log.e("myException", e.getMessage());
                    }
                    // Now assign the values and count to the FilterResults object
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
    }


    private class DownloadShippers extends AsyncTask<String, Void, ArrayList<StockInfo>> {

        @Override
        protected ArrayList<StockInfo> doInBackground(String... constraint) {
            ArrayList<StockInfo> shippersNames = new ArrayList<>();

            try {
                String rawJson = downloadUrl("http://d.yimg.com/aq/autoc?query="+constraint[0]+"&region=IN&lang=en-us");
                JSONObject mainObject = new JSONObject(rawJson);
                JSONObject outerResultSet = mainObject.getJSONObject("ResultSet");
                JSONArray innerResultSet = outerResultSet.getJSONArray("Result");

                for (int i=0; i<innerResultSet.length(); i++) {
                    JSONObject res = innerResultSet.getJSONObject(i);
                    if (res.getString("typeDisp").equals("Equity")) {
                        StockInfo stock = new StockInfo(res.getString("symbol"), res.getString("name"));
                        shippersNames.add(stock);
                    }

                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }


            return shippersNames;
        }

        @Override
        protected void onPostExecute(ArrayList<StockInfo> result) {

        }

    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream;
        HttpURLConnection urlConnection;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();
            iStream.close();
            urlConnection.disconnect();

        } catch (Exception ignored){
            //@todo
        }
        return data;
    }

}