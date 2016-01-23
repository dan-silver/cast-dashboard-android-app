package com.example.dan.castdemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.WidgetViewHolder> {

    public void addWidget(Widget w) {
        widgetList.add(w);
    }

    public class WidgetViewHolder extends RecyclerView.ViewHolder {
        protected TextView topHeader;
        protected TextView bottomHeader;

        public WidgetViewHolder(View view) {
            super(view);
            this.topHeader = (TextView) view.findViewById(R.id.widget_name);
            this.bottomHeader = (TextView) view.findViewById(R.id.widget_type);
        }
    }

    private List<Widget> widgetList;

    public MyRecyclerAdapter(List<Widget> widgetList) {
        this.widgetList = widgetList;
    }

    @Override
    public WidgetViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);

        return new WidgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WidgetViewHolder customViewHolder, int i) {
        Widget widget = widgetList.get(i);

        customViewHolder.topHeader.setText(widget.getClass().getSimpleName());
        customViewHolder.bottomHeader.setText("Some specific identifying detail");
    }

    @Override
    public int getItemCount() {
        return (null != widgetList ? widgetList.size() : 0);
    }
}