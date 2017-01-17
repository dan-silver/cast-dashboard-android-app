package com.silver.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.silver.dan.castdemo.MainActivity;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.RecyclerItemClickListener;
import com.silver.dan.castdemo.StockInfo;
import com.silver.dan.castdemo.WidgetOption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StocksSettings extends WidgetSettingsFragment {

    public static String STOCK_IN_LIST = "STOCK_IN_LIST";
    ArrayList<StockInfo> stocks = new ArrayList<>();
    final StockListAdapter stockListAdapter = new StockListAdapter(stocks);

    @BindView(R.id.select_stock)
    AutoCompleteTextView addStock;

    @BindView(R.id.stock_list)
    RecyclerView stockList;

    WidgetOption optionSavedStocks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stocks_settings, container, false);
        ButterKnife.bind(this, view);
        initView();

        return view;
    }

    @Override
    public void initView() {

        optionSavedStocks = loadOrInitOption(StocksSettings.STOCK_IN_LIST);

        supportWidgetHeightOption();
        supportWidgetScrollInterval();
        supportWidgetRefreshInterval();

        final GetStockSuggestionsAdapter dropDownMenuAdapter = new GetStockSuggestionsAdapter(getContext());

        addStock.setAdapter(dropDownMenuAdapter);

        addStock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StockInfo selectedStock = dropDownMenuAdapter.getItem(position);

                if (selectedStock == null) {
                    return;
                }
                String ticker = selectedStock.getTicker();
                List<String> stockTickers = optionSavedStocks.getList();

                boolean isNewStock = true;
                for (String savedTicker : stockTickers) {
                    if (savedTicker.equals(ticker)) {
                        isNewStock = false;
                        break;
                    }
                }


                if (isNewStock) {
                    stockTickers.add(ticker);
                    optionSavedStocks.update(stockTickers);
                    stockListAdapter.addStock(selectedStock);
                }


                addStock.clearListSelection();
                addStock.setText("");

                refreshWidget();
            }
        });

        stockList.setAdapter(stockListAdapter);
        stockList.setLayoutManager(new LinearLayoutManager(getContext()));

        stockList.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                final StockInfo stock = stockListAdapter.getStock(position);
                new MaterialDialog.Builder(getContext())
                        .title("Remove " + stock.getName() + "?")
                        .positiveText(R.string.remove)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                stockListAdapter.deleteStock(position);


                                String ticker = stock.getTicker();

                                List<String> stockTickers = optionSavedStocks.getList();

                                if (stockTickers.contains(ticker)) {
                                    stockTickers.remove(ticker);
                                    optionSavedStocks.update(stockTickers);
                                }

                                refreshWidget();
                            }
                        })
                        .show();
            }
        }));

        // query the db to get the saved stocks
        if (optionSavedStocks.getList().size() > 0) {

            String tickerQueryStr = "";
            for (String ticker : optionSavedStocks.getList()) {
                tickerQueryStr += ticker + "+";
            }

            Ion.with(getContext())
                    .load("http://finance.yahoo.com/d/quotes.csv?s=" + tickerQueryStr + "&f=sn")
                    .asInputStream()
                    .setCallback(new FutureCallback<InputStream>() {
                        @Override
                        public void onCompleted(Exception e, InputStream is) {
                            if (e != null) {
                                Log.e(MainActivity.TAG, e.toString());
                                return;
                            }


                            List<StockInfo> stocksToList = new ArrayList<>();
                            CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(is)));
                            try {
                                List stockLines = reader.readAll();
                                for (int i = 0; i < stockLines.size(); i++) {
                                    String[] parsedStockLine = (String[]) stockLines.get(i);
                                    StockInfo info = new StockInfo(parsedStockLine[0], parsedStockLine[1]);
                                    stocksToList.add(info);
                                }
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            stockListAdapter.addStocks(stocksToList);
                        }
                    });
        }


    }

    public class StockListAdapter extends RecyclerView.Adapter<StockListAdapter.ViewHolder> {
        private ArrayList<StockInfo> mDataset;

        void addStock(StockInfo stock) {
            ArrayList<StockInfo> stocks = new ArrayList<>();
            stocks.add(stock);
            addStocks(stocks);
        }

        void addStocks(final List<StockInfo> stocks) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDataset.addAll(0, stocks);
                    notifyDataSetChanged();
                }
            });
        }

        void deleteStock(int position) {
            mDataset.remove(position);
            notifyDataSetChanged();
        }

        StockInfo getStock(int position) {
            return mDataset.get(position);
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView mCompanyName;
            TextView mStockTicker;


            ViewHolder(View v) {
                super(v);
                mStockTicker = (TextView) v.findViewById(R.id.stock_ticker);
                mCompanyName = (TextView) v.findViewById(R.id.company_name);
            }
        }

        StockListAdapter(ArrayList<StockInfo> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public StockListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_stocks_list_item, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mStockTicker.setText(mDataset.get(position).getTicker());
            holder.mCompanyName.setText(mDataset.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}