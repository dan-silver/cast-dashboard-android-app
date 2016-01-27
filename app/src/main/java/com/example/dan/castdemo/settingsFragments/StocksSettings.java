package com.example.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dan.castdemo.CUSTOMContactsCompletionView;
import com.example.dan.castdemo.R;
import com.example.dan.castdemo.Stock;
import com.example.dan.castdemo.Widget;
import com.example.dan.castdemo.Widget_Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.tokenautocomplete.FilteredArrayAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StocksSettings extends Fragment {

    private Widget widget;

    @Bind(R.id.stock_search_view)
    CUSTOMContactsCompletionView addStock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        long widgetId = bundle.getLong(Widget.ID, -1);

        // lookup widget in the database
        // display appropriate settings for that widget type
        widget = new Select().from(Widget.class).where(Widget_Table.id.eq(widgetId)).querySingle();


        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stocks_settings, container, false);
        ButterKnife.bind(this, view);


        ArrayAdapter<Stock> adapter = new FilteredArrayAdapter<Stock>(getContext(), R.layout.stock_auto_complete_dropdown, Stock.getAllStocks()) {
            @Override
            protected boolean keepObject(Stock obj, String mask) {
                mask = mask.toLowerCase();
                return obj.name.toLowerCase().contains(mask) || obj.ticker.toLowerCase().contains(mask);
            }


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Stock user = getItem(position);

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.stock_auto_complete_dropdown, parent, false);
                }

                TextView companyName = (TextView) convertView.findViewById(R.id.company_name);
                TextView stockTicker = (TextView) convertView.findViewById(R.id.stock_ticker);

                companyName.setText(user.name);
                stockTicker.setText(user.ticker);

                return convertView;
            }
        };


        addStock.setAdapter(adapter);


        return view;
    }

    public static void init(Widget widget) {

    }


}
