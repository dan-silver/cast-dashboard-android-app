package com.silver.dan.castdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.silver.dan.castdemo.widgetList.OnDragListener;
import com.silver.dan.castdemo.widgetList.SimpleItemTouchHelperCallback;
import com.silver.dan.castdemo.widgets.CanBeCreatedListener;
import com.silver.dan.castdemo.widgets.UIWidget;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class WidgetList extends Fragment implements OnDragListener {

    ArrayList<CanBeCreatedListener> widgetCanBeCreatedListeners = new ArrayList<>();

    @BindView(R.id.widgetList)
    RecyclerView widgetList;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout mSwipeContainer;

    private WidgetListAdapter adapter;

    public WidgetList() {

    }

    private ItemTouchHelper mItemTouchHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new WidgetListAdapter(MainActivity.dashboard.widgets, (MainActivity) getActivity(), this, widgetCanBeCreatedListeners);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_list, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        widgetList.setAdapter(adapter);
        widgetList.setLayoutManager(new LinearLayoutManager(getContext()));
        widgetList.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(widgetList);


        mSwipeContainer.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.ccl_grey600));
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.dashboard.clearData();
                adapter.notifyDataSetChanged();
                MainActivity.dashboard.loadFromFirebase(view.getContext(), new Dashboard.OnLoadCallback() {
                    @Override
                    public void onReady() {
                        mSwipeContainer.setRefreshing(false);
                        adapter.notifyItemRangeInserted(0, adapter.getItemCount());
                    }

                    @Override
                    public void onError() {
                        mSwipeContainer.setRefreshing(false);
                    }
                });
            }
        });
    }

    @OnClick(R.id.fab)
    public void addWidget() {
        final ArrayList<Widget.WidgetType> widgetTypes = new ArrayList<Widget.WidgetType>() {{
            add(Widget.WidgetType.GOOGLE_CALENDAR);
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

                                adapter.addWidget(widget);

                                CastCommunicator.sendWidget(widget, getContext());
                            }
                        };


                        UIWidget uiWidget = widget.getUIWidget(getContext());
                        if (uiWidget.canBeCreated()) {
                            listener.onCanBeCreated();
                        } else {
                            // request the permissions for the widget, and set the callback key
                            int permissionRequestCallbackKey = uiWidget.requestPermissions(getActivity());
                            listener.setRequestCallbackReturnCode(permissionRequestCallbackKey);

                            widgetCanBeCreatedListeners.add(listener);
                        }

                        return true;
                    }
                })
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshSecondaryTitles();
        ((MainActivity) getActivity()).setDrawerItemChecked(MainActivity.NAV_VIEW_WIDGETS_ITEM);
    }

    public void processPermissionReceivedCallback(int key, boolean permissionGranted) {
        for (Iterator<CanBeCreatedListener> iterator = widgetCanBeCreatedListeners.iterator(); iterator.hasNext(); ) {
            CanBeCreatedListener listener = iterator.next();
            if (listener.ifConditionsAreMet(key)) {
                if (permissionGranted) {
                    listener.onCanBeCreated();
                } else {
                    iterator.remove(); //removes from list and iterator to prevent double access
                }
            }
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public void deleteWidget(Widget widget) {
        adapter.deleteWidget(widget);
    }
}