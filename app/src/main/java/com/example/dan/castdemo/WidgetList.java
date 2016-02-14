package com.example.dan.castdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.dan.castdemo.widgetList.OnDragListener;
import com.example.dan.castdemo.widgetList.SimpleItemTouchHelperCallback;
import com.example.dan.castdemo.widgets.CalendarWidget;
import com.example.dan.castdemo.widgets.ClockWidget;
import com.example.dan.castdemo.widgets.MapWidget;
import com.example.dan.castdemo.widgets.PlaceholderWidget;
import com.example.dan.castdemo.widgets.StocksWidget;
import com.example.dan.castdemo.widgets.WeatherWidget;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.SelectListTransaction;
import com.raizlabs.android.dbflow.runtime.transaction.TransactionListenerAdapter;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WidgetList extends Fragment implements OnDragListener {

    MainActivity activity;

    @Bind(R.id.widgetList)
    RecyclerView widgetList;

    public WidgetList() {

    }

    private ItemTouchHelper mItemTouchHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_list, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        widgetList.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @OnClick(R.id.fab)
    public void addWidget() {
        new MaterialDialog.Builder(getContext())
                .title("New Widget")
                .items(new String[]{CalendarWidget.HUMAN_NAME, StocksWidget.HUMAN_NAME, MapWidget.HUMAN_NAME, ClockWidget.HUMAN_NAME, WeatherWidget.HUMAN_NAME})
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Widget widget = new Widget();
                        widget.setType(Widget.types.valueOf(text.toString().toUpperCase()));

                        widget.position = widgetList.getAdapter().getItemCount();

                        widget.save();

                        widget.initOptions();
                        refreshList();
                        CastCommunicator.sendWidget(widget);

                        return true;
                    }
                })
                .positiveText("Add")
                .show();
    }

    @Override
    public void onResume() {
        activity = (MainActivity) getActivity();
        activity.setDrawerItemChecked(MainActivity.NAV_VIEW_WIDGETS_ITEM);

        refreshList();
        super.onResume();
    }

    public void refreshList() {
        final WidgetList ctx = this;

        // async fetch all saved widgets
        Widget.fetchAll(new FetchAllWidgetsListener() {
            @Override
            public void results(List<Widget> widgets) {
                WidgetListAdapter adapter = new WidgetListAdapter(widgets, activity, ctx);

                widgetList.setAdapter(adapter);

                ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
                mItemTouchHelper = new ItemTouchHelper(callback);
                mItemTouchHelper.attachToRecyclerView(widgetList);
            }
        });
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}