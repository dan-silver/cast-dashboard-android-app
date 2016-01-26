package com.example.dan.castdemo;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

public class CUSTOMContactsCompletionView extends TokenCompleteTextView<Stock> {
    public CUSTOMContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Stock stock) {

        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout) l.inflate(R.layout.stock_preview, (ViewGroup) CUSTOMContactsCompletionView.this.getParent(), false);
        ((TextView) view.findViewById(R.id.stock_name)).setText(stock.name);

        return view;
    }

    @Override
    protected Stock defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
        int index = completionText.indexOf('@');
        if (index == -1) {
            return new Stock(completionText, completionText.replace(" ", "") + "@example.com");
        } else {
            return new Stock(completionText.substring(0, index), completionText);
        }
    }

}
