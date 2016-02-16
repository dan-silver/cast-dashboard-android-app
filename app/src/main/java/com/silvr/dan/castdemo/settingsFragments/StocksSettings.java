package com.silvr.dan.castdemo.settingsFragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.silvr.dan.castdemo.R;
import com.silvr.dan.castdemo.RecyclerItemClickListener;
import com.silvr.dan.castdemo.Stock;
import com.silvr.dan.castdemo.StockInfo;
import com.silvr.dan.castdemo.Stock_Table;
import com.silvr.dan.castdemo.Widget;
import com.silvr.dan.castdemo.WidgetOption;
import com.silvr.dan.castdemo.WidgetOption_Table;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StocksSettings extends WidgetSettingsFragment {

    public static String STOCK_IN_LIST = "STOCK_IN_LIST";
    ArrayList<StockInfo> stocks = new ArrayList<>();
    final StockListAdapter stockListAdapter = new StockListAdapter(stocks);


    @Bind(R.id.select_stock)
    AutoCompleteTextView addStock;

    @Bind(R.id.stock_list)
    RecyclerView stockList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Stock.insertAllStocks(getContext());

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stocks_settings, container, false);
        ButterKnife.bind(this, view);


        final SimpleCursorAdapter dropDownMenuAdapter = new SimpleCursorAdapter(getContext(), R.layout.stock_auto_complete_dropdown, null,
                new String[]{"name", "ticker"},
                new int[]{R.id.company_name, R.id.stock_ticker},
                0);

        dropDownMenuAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence hint) {

                ConditionGroup query = ConditionGroup.clause().orAll(Stock_Table.name.like("%" + hint + "%"), Stock_Table.ticker.like("%" + hint + "%"));
                return new Select().from(Stock.class).where(query).query();
            }
        });

        SimpleCursorAdapter.CursorToStringConverter converter = new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                int desiredColumn = cursor.getColumnIndex("name");
                return cursor.getString(desiredColumn);
            }
        };

        dropDownMenuAdapter.setCursorToStringConverter(converter);

        addStock.setAdapter(dropDownMenuAdapter);

        addStock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Stock selectedStock = new Select().from(Stock.class).where(Stock_Table._id.is(id)).querySingle();


                WidgetOption stockDbRecord = new WidgetOption();
                stockDbRecord.key = STOCK_IN_LIST;
                stockDbRecord.value = Long.toString(selectedStock.get_id());
                stockDbRecord.associateWidget(widget);
                stockDbRecord.save();

                stockListAdapter.addStock(selectedStock);
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
                        .positiveText("Remove")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                stockListAdapter.deleteStock(position);


                                ConditionGroup conditions = ConditionGroup.clause();
                                conditions.andAll(WidgetOption_Table.widgetForeignKeyContainer_id.eq(widget.id),
                                        WidgetOption_Table.key.is(STOCK_IN_LIST), WidgetOption_Table.value.is(Long.toString(stock.getId())));

                                new Delete().from(WidgetOption.class).where(conditions).execute();
                                refreshWidget();
                            }
                        })
                        .show();
            }
        }));

        // query the db to get the saved stocks
        List<WidgetOption> savedStocks = widget.getOptions(STOCK_IN_LIST);

        for (WidgetOption option : savedStocks) {
            long stockId = Long.parseLong(option.value);

            Stock stock = new Select().from(Stock.class).where(Stock_Table._id.is(stockId)).querySingle();

            StockInfo info = new StockInfo(stock.getTicker(), stock.getName(), stock.get_id());

            stockListAdapter.addStock(info);
        }


        return view;
    }

    public class StockListAdapter extends RecyclerView.Adapter<StockListAdapter.ViewHolder> {
        private ArrayList<StockInfo> mDataset;

        public void addStock(Stock selectedStock) {
            StockInfo stock = new StockInfo(selectedStock.getTicker(), selectedStock.getName(), selectedStock.get_id());
            addStock(stock);
        }

        public void addStock(StockInfo stock) {
            mDataset.add(0, stock);
            notifyDataSetChanged();
        }

        public void deleteStock(int position) {
            mDataset.remove(position);
            notifyDataSetChanged();
        }

        public StockInfo getStock(int position) {
            return mDataset.get(position);
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mCompanyName;
            public TextView mStockTicker;


            public ViewHolder(View v) {
                super(v);
                mStockTicker = (TextView) v.findViewById(R.id.stock_ticker);
                mCompanyName = (TextView) v.findViewById(R.id.company_name);
            }
        }

        public StockListAdapter(ArrayList<StockInfo> myDataset) {
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

    public static void init(Widget widget) {

    }
}