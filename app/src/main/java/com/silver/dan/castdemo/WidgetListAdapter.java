package com.silver.dan.castdemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.silver.dan.castdemo.widgetList.ItemTouchHelperAdapter;
import com.silver.dan.castdemo.widgetList.ItemTouchHelperViewHolder;
import com.silver.dan.castdemo.widgetList.OnDragListener;
import com.silver.dan.castdemo.widgets.CanBeCreatedListener;
import com.silver.dan.castdemo.widgets.UIWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;


class WidgetListAdapter extends RecyclerView.Adapter<WidgetListAdapter.WidgetViewHolder> implements ItemTouchHelperAdapter {

    private final MainActivity mainActivity;
    private final ArrayList<CanBeCreatedListener> widgetCanBeCreatedListeners; // ref to WidgetList.javas copy
    private List<Widget> widgetList;
    private final OnDragListener mDragStartListener;

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(widgetList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mainActivity.onItemMoved(saveWidgetsOrder());
            }
        });
        syncWidgetPositions();
    }

    WidgetListAdapter(List<Widget> widgetList, MainActivity activity, OnDragListener dragStartListener, ArrayList<CanBeCreatedListener> widgetCanBeCreatedListeners) {
        this.widgetList = widgetList;
        this.mainActivity = activity;
        mDragStartListener = dragStartListener;
        this.widgetCanBeCreatedListeners = widgetCanBeCreatedListeners;

    }

    @Override
    public void onItemDismiss(int position) {

    }

    private JSONObject saveWidgetsOrder() {
        JSONObject widgetOrder = new JSONObject();
        int i = 0;
        try {
            for (Widget widget : widgetList) {
                widget.position = i;
                widgetOrder.put(widget.guid, widget.position);
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return widgetOrder;
    }


    // should only be necessary on widget delete, so there's not a gap in the orders
    // important since current widget position for new ones is the length of the list
    private void syncWidgetPositions() {
        if (widgetList == null)
            return;

        int i = 0;
        for (Widget widget : widgetList) {
            widget.position = i;
            widget.savePosition();
            i++;
        }
    }

    void addWidget(Widget widget) {
        this.widgetList.add(widget);
        notifyItemInserted(widgetList.indexOf(widget));
    }

    void deleteWidget(Widget widget) {
        int index = widgetList.indexOf(widget);
        notifyItemRemoved(index);
        this.widgetList.remove(widget);
        widget.delete();
    }


    class WidgetViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        TextView topHeader;
        TextView bottomHeader;
        ImageView typeIcon;
        View listItemView;
        ImageView handleView;


        WidgetViewHolder(View view) {
            super(view);
            this.topHeader = (TextView) view.findViewById(R.id.widget_name);
            this.bottomHeader = (TextView) view.findViewById(R.id.widget_type);
            this.typeIcon = (ImageView) view.findViewById(R.id.widget_type_icon);
            handleView = (ImageView) itemView.findViewById(R.id.widget_handle);
            this.listItemView = view;
        }

        @Override
        public void onItemSelected() {
            int color = ContextCompat.getColor(listItemView.getContext(), R.color.list_item_drag_highlight);
            itemView.setBackgroundColor(color);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    @Override
    public WidgetViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row, viewGroup, false);

        return new WidgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WidgetViewHolder customViewHolder, int i) {
        final Widget widget = widgetList.get(i);
        final UIWidget uiWidget = widget.getUIWidget(mainActivity);

        customViewHolder.topHeader.setText(mainActivity.getApplicationContext().getString(widget.getHumanNameRes()));
        customViewHolder.bottomHeader.setText(uiWidget.getWidgetPreviewSecondaryHeader());


        customViewHolder.typeIcon.setImageResource(widget.getIconResource());

        customViewHolder.listItemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CanBeCreatedListener listener = new CanBeCreatedListener() {
                    @Override
                    public void onCanBeCreated() {
                        Intent intent = new Intent(mainActivity, WidgetSettingsActivity.class);
                        intent.putExtra(Widget.GUID, widget.guid);

                        mainActivity.startActivityForResult(intent, WidgetSettingsActivity.REQUEST_CODE);

                    }
                };
                uiWidget.setOnCanBeCreatedOrEditedListener(listener);

                if (!uiWidget.canBeCreated()) {
                    widgetCanBeCreatedListeners.add(listener);
                }
            }
        });

        customViewHolder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (MotionEventCompat.getActionMasked(event)) {
                    case (MotionEvent.ACTION_DOWN):
                        mDragStartListener.onStartDrag(customViewHolder);
                        break;
                }

                return false;
            }


        });


    }

    @Override
    public int getItemCount() {
        return (null != widgetList ? widgetList.size() : 0);
    }
}