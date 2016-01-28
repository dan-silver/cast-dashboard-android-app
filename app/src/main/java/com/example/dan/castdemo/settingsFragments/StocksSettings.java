package com.example.dan.castdemo.settingsFragments;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleCursorAdapter;

import com.example.dan.castdemo.R;
import com.example.dan.castdemo.Stock;
import com.example.dan.castdemo.StockCompletionView;
import com.example.dan.castdemo.StockInfo;
import com.example.dan.castdemo.Stock_Table;
import com.example.dan.castdemo.Widget;
import com.example.dan.castdemo.WidgetOption;
import com.example.dan.castdemo.WidgetOption_Table;
import com.example.dan.castdemo.Widget_Table;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Insert;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.List;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StocksSettings extends Fragment {

    private Widget widget;

    @Bind(R.id.select_stock)
    MultiAutoCompleteTextView addStock;
    static String STOCK_IN_LIST = "STOCK_IN_LIST";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        long widgetId = bundle.getLong(Widget.ID, -1);

        // lookup widget in the database
        // display appropriate settings for that widget type
        widget = new Select().from(Widget.class).where(Widget_Table.id.eq(widgetId)).querySingle();

        Stock.insertAllStocks(getContext());

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stocks_settings, container, false);
        ButterKnife.bind(this, view);


        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(), R.layout.stock_auto_complete_dropdown, null,
                new String[]{"name", "ticker"},
                new int[]{R.id.company_name, R.id.stock_ticker},
                0);

        adapter.setFilterQueryProvider(new FilterQueryProvider() {
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

        adapter.setCursorToStringConverter(converter);

        addStock.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        addStock.setThreshold(1);
        addStock.setAdapter(adapter);

        addStock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        // query the db to get the saved stocks
        ConditionGroup conditions = new ConditionGroup();
        conditions.orAll(WidgetOption_Table.widgetForeignKeyContainer_id.eq(widget.id), WidgetOption_Table.key.is(STOCK_IN_LIST));

        Cursor cursor = SQLite.select()
                .from(WidgetOption.class)
                .where(conditions)
                .query();
//        while (cursor.moveToNext()) {
//            ((SimpleCursorAdapter) addStock.getAdapter()).(cursor);
//        }
        cursor.close();

        return view;
    }

    public static void init(Widget widget) {

    }

//    @Override
//    public void onTokenAdded(Object token) {
//        WidgetOption stockDbRecord = new WidgetOption();
//
//        Cursor cursor = (Cursor) token;
//        cursor.moveToFirst();
//
//        stockDbRecord.key = STOCK_IN_LIST;
//        stockDbRecord.value = cursor.getString(cursor.getColumnIndex("ticker"));
//        stockDbRecord.associateWidget(widget);
//        stockDbRecord.save();
//
//        //save to the database
//    }

//    @Override
//    public void onTokenRemoved(Object token) {
//        Cursor cursor = (Cursor) token;
//        cursor.moveToFirst();
//
//        String ticker = cursor.getString(cursor.getColumnIndex("ticker"));
//
//        new Delete().from(WidgetOption.class).where(WidgetOption_Table.key.is(ticker)).query();
//        //remove from database
//    }
}