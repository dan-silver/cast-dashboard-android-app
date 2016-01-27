package com.example.dan.castdemo;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

public class StockCompletionView extends TokenCompleteTextView<Object> {
    public StockCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Object stockObj) {
        // manual cast required https://github.com/splitwise/TokenAutoComplete/issues/159
//        StockInfo stock = (Cursor) stockObj;

        String stockName = ((Cursor) stockObj).getString(((Cursor) stockObj).getColumnIndex("name"));

        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout) l.inflate(R.layout.stock_preview, (ViewGroup) StockCompletionView.this.getParent(), false);
        ((TextView) view.findViewById(R.id.stock_name)).setText(stockName);

        return view;
    }


    //@todo return StockInfo instead of object here
    @Override
    protected Object defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
        int index = completionText.indexOf('@');
        if (index == -1) {
            return new StockInfo(completionText, completionText.replace(" ", "") + "@example.com");
        } else {
            return new StockInfo(completionText.substring(0, index), completionText);
        }
    }


//
//    @Override
//    protected View getViewForObject(StockInfo person) {
//
//        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//        LinearLayout view = (LinearLayout)l.inflate(R.layout.stock_auto_complete_dropdown, (ViewGroup)StockCompletionView.this.getParent(), false);
//
//                TextView companyName = (TextView) view.findViewById(R.id.company_name);
//                TextView stockTicker = (TextView) view.findViewById(R.id.stock_ticker);
//
//                companyName.setText(person.getName());
//                stockTicker.setText(person.getTicker());
//
//        return view;
//    }

}
