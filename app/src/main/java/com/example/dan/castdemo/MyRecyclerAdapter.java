package com.example.dan.castdemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.WidgetViewHolder> {

    public void addWidget(Widget w) {
        feedItemList.add(w);
    }

    public class WidgetViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView type;

        public WidgetViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.widget_name);
            this.type = (TextView) view.findViewById(R.id.widget_type);
        }
    }

    private List<Widget> feedItemList;

    public MyRecyclerAdapter(List<Widget> feedItemList) {
        this.feedItemList = feedItemList;
    }

    @Override
    public WidgetViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);

        return new WidgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WidgetViewHolder customViewHolder, int i) {
        Widget widget = feedItemList.get(i);

        customViewHolder.name.setText(widget.name);
        customViewHolder.type.setText(widget.getClass().getSimpleName());
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
}