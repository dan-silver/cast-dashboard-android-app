package com.silver.dan.castdemo;

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
import com.silver.dan.castdemo.widgetList.OnDragListener;
import com.silver.dan.castdemo.widgetList.SimpleItemTouchHelperCallback;
import com.silver.dan.castdemo.widgets.CanBeCreatedListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WidgetList extends Fragment implements OnDragListener {

    ArrayList<CanBeCreatedListener> widgetCanBeCreatedListeners = new ArrayList<>();

    @BindView(R.id.widgetList)
    RecyclerView widgetList;

    static List<Widget> widgetsCache;

    public WidgetList() {

    }

    private ItemTouchHelper mItemTouchHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_list, container, false);
        ButterKnife.bind(this, view);


        // initially populate this list with an empty widget list
        WidgetListAdapter adapter = new WidgetListAdapter(null, (MainActivity) getActivity(), this, widgetCanBeCreatedListeners);
        widgetList.setAdapter(adapter);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        widgetList.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @OnClick(R.id.fab)
    public void addWidget() {
        final ArrayList<Widget.WidgetType> widgetTypes = new ArrayList<Widget.WidgetType>() {{
            add(Widget.WidgetType.CALENDAR);
            add(Widget.WidgetType.CLOCK);
            add(Widget.WidgetType.COUNTDOWN);
            add(Widget.WidgetType.CUSTOM_TEXT);
            add(Widget.WidgetType.MAP);
            add(Widget.WidgetType.RSS);
            add(Widget.WidgetType.STOCKS);
            add(Widget.WidgetType.WEATHER);
        }};

        new MaterialDialog.Builder(getContext())
                .title("New Widget")
                .items(R.array.newWidgetDialogList)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        final Widget widget = new Widget();

                        widget.setType(widgetTypes.get(which));
                        widget.position = widgetList.getAdapter().getItemCount();


                        /*
                         *
                         * The idea here is to have a callback for when the widget can be created.
                         * If there are no widget creation requirements, such as extra app runtime permissions,
                         * create it right away. Otherwise save the callback so it can be executed when the widget
                         * can be created.
                         *
                         */
                        CanBeCreatedListener listener = new CanBeCreatedListener() {
                            @Override
                            public void onCanBeCreated() {
                                widget.save();

                                widget.initWidgetSettings(getContext());
                                refreshList();
                                CastCommunicator.sendWidget(widget);

                            }
                        };
                        widget.getUIWidget(getContext()).setOnCanBeCreatedOrEditedListener(listener);

                        if (!widget.getUIWidget(getContext()).canBeCreated()) {
                            widgetCanBeCreatedListeners.add(listener);
                        }

                        return true;
                    }
                })
                .show();
    }

    @Override
    public void onResume() {
        ((MainActivity) getActivity()).setDrawerItemChecked(MainActivity.NAV_VIEW_WIDGETS_ITEM);

        refreshList();
        super.onResume();
    }

    public void processPermissionReceivedCallback(int key, boolean permissionGranted) {
        for (Iterator<CanBeCreatedListener> iterator = widgetCanBeCreatedListeners.iterator(); iterator.hasNext(); ) {
            CanBeCreatedListener listener = iterator.next();
            if (listener.checkIfConditionsAreMet(key)) {
                if (permissionGranted) {
                    listener.onCanBeCreated();
                } else {
                    iterator.remove(); //removes from list and iterator to prevent double access
                }
            }
        }
    }

    public void refreshList() {

        // async fetch all saved widgets
        Widget.fetchAll(new FetchAllWidgetsListener() {
            @Override
            public void results(List<Widget> widgets) {
                // manually order the list
                widgetsCache = widgets;

                WidgetListAdapter adapter = (WidgetListAdapter) widgetList.getAdapter();

                adapter.setWidgetList(widgets);
                adapter.notifyDataSetChanged();



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