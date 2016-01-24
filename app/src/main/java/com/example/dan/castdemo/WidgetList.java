package com.example.dan.castdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.SelectListTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.TransactionListenerAdapter;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WidgetList extends Fragment {

    List<Widget> widgets = new ArrayList<>();


    WidgetListAdapter adapter;
    @Bind(R.id.widgetList)
    RecyclerView widgetList;


    public WidgetList() {

        refreshList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_list, container, false);
        ButterKnife.bind(this, view);

        adapter = new WidgetListAdapter(widgets, (MainActivity) getActivity());
        widgetList.setAdapter(adapter);
        widgetList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();

        return view;
    }

    @OnClick(R.id.fab)
    public void addWidget() {
        final MainActivity activity = (MainActivity) getActivity();

        new MaterialDialog.Builder(getContext())
                .title("Widget Type")
                .items(Widget.widgetNames)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Widget widget = new Widget();
                        widget.setType(which);

                        widget.insert();

                        adapter.addWidget(widget);
                        adapter.notifyDataSetChanged();

                        activity.sendMessage(widget.getHumanName() + " widget created.");
//                        activity.switchToFragment(new WidgetSettings(), true);
                        return true;
                    }
                })
                .positiveText("Add")
                .show();
    }

    @Override
    public void onResume() {
        refreshList();
        super.onResume();
    }

    public void refreshList() {
        // async fetch all saved widgets
        TransactionManager.getInstance().addTransaction(
                new SelectListTransaction<>(new Select().from(Widget.class),
                        new TransactionListenerAdapter<List<Widget>>() {
                            @Override
                            public void onResultReceived(List<Widget> someObjectList) {
                                widgets.clear();
                                widgets.addAll(someObjectList);

                                if (adapter != null)
                                    adapter.notifyDataSetChanged();
                            }
                        }));

    }
}