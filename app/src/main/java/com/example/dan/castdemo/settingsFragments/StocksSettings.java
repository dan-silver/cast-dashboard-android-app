package com.example.dan.castdemo.settingsFragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;

import com.example.dan.castdemo.MainActivity;
import com.example.dan.castdemo.R;
import com.example.dan.castdemo.Stock;
import com.example.dan.castdemo.StockCompletionView;
import com.example.dan.castdemo.Stock_Table;
import com.example.dan.castdemo.Widget;
import com.example.dan.castdemo.Widget_Table;
import com.raizlabs.android.dbflow.list.FlowCursorList;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StocksSettings extends Fragment {

    private Widget widget;

    @Bind(R.id.select_stock)
    StockCompletionView addStock;

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


        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(), R.layout.stock_auto_complete_dropdown, null,
                new String[] {"name", "ticker"},
                new int[] {R.id.company_name, R.id.stock_ticker},
                0);


        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence hint) {
                //@todo .where.or on ticker column
                return (new Select().from(Stock.class)).where(Stock_Table.name.like("%" + hint + "%")).query();
            }
        });

        addStock.setAdapter(adapter);


        return view;
    }

    public static void init(Widget widget) {

    }

}
