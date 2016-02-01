package com.example.dan.castdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dan.castdemo.widgets.CalendarWidget;
import com.example.dan.castdemo.widgets.MapWidget;
import com.example.dan.castdemo.widgets.PlaceholderWidget;
import com.example.dan.castdemo.widgets.StocksWidget;
import com.example.dan.castdemo.widgets.UIWidget;

import java.util.List;


public class WidgetListAdapter extends RecyclerView.Adapter<WidgetListAdapter.WidgetViewHolder> {

    private final MainActivity mainActivity;

    public class WidgetViewHolder extends RecyclerView.ViewHolder {
        protected TextView topHeader;
        protected TextView bottomHeader;
        protected ImageView typeIcon;
        protected View listItemView;

        public WidgetViewHolder(View view) {
            super(view);
            this.topHeader = (TextView) view.findViewById(R.id.widget_name);
            this.bottomHeader = (TextView) view.findViewById(R.id.widget_type);
            this.typeIcon = (ImageView) view.findViewById(R.id.widget_type_icon);
            this.listItemView = view;
        }
    }

    private List<Widget> widgetList;

    public WidgetListAdapter(List<Widget> widgetList, MainActivity activity) {
        this.widgetList = widgetList;
        this.mainActivity = activity;
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
        Widget.types widgetType = widget.getWidgetType();
        UIWidget uiWidget;
        if (widgetType == Widget.types.CALENDAR) {
            uiWidget = new CalendarWidget(mainActivity, widget);
        } else if (widgetType == Widget.types.STOCKS) {
            uiWidget = new StocksWidget(mainActivity, widget);
        } else if (widgetType == Widget.types.MAP) {
            uiWidget = new MapWidget(mainActivity, widget);
        } else {
            uiWidget = new PlaceholderWidget(mainActivity, widget);
        }

        customViewHolder.topHeader.setText(widget.getHumanName());
        customViewHolder.bottomHeader.setText(uiWidget.getWidgetPreviewSecondaryHeader());


        customViewHolder.typeIcon.setImageResource(widget.getIconResource());

        customViewHolder.listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new WidgetSettings();

                Bundle bundle = new Bundle();
                bundle.putLong(Widget.ID, widget.id);
                fragment.setArguments(bundle);

                mainActivity.switchToFragment(fragment, true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != widgetList ? widgetList.size() : 0);
    }
}