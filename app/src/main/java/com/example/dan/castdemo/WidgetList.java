package com.example.dan.castdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.dan.castdemo.widgets.CalendarWidget;
import com.example.dan.castdemo.widgets.PlaceHolderWidget;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WidgetList extends Fragment {

    MyRecyclerAdapter adapter;
    @Bind(R.id.widgetList) RecyclerView widgetList;

    public WidgetList() {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(new PlaceHolderWidget());
        widgets.add(new PlaceHolderWidget());

        adapter = new MyRecyclerAdapter(widgets);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_list, container, false);
        ButterKnife.bind(this, view);

        widgetList.setAdapter(adapter);
        widgetList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();

        return view;
    }

    @OnClick(R.id.fab)
    public void addWidget() {
        final MainActivity activity = (MainActivity) getActivity();

        final Class[] widgetTypes = new Class[] {PlaceHolderWidget.class, CalendarWidget.class};

        String[] widgetNames = new String[widgetTypes.length];

        for (int i=0; i< widgetTypes.length; i++)
            widgetNames[i] = widgetTypes[i].getSimpleName();


        new MaterialDialog.Builder(getContext())
                .title("Widget Type")
                .items(widgetNames)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Widget w = Widget.createFromClass(widgetTypes[which]);

                        adapter.addWidget(w);
                        adapter.notifyDataSetChanged();

                        activity.sendMessage(widgetTypes[which].getSimpleName() + " created.");

                        return true;
                    }
                })
                .positiveText("Add")
                .show();
    }
}