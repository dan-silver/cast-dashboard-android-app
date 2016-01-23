package com.example.dan.castdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.method.CharacterPickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class WidgetListAdapter extends RecyclerView.Adapter<WidgetListAdapter.WidgetViewHolder> {

    private final MainActivity mainActivity;

    public void addWidget(Widget w) {
        widgetList.add(w);
    }

    public class WidgetViewHolder extends RecyclerView.ViewHolder {
        protected TextView topHeader;
        protected TextView bottomHeader;
        protected ImageView editIcon;

        public WidgetViewHolder(View view) {
            super(view);
            this.topHeader = (TextView) view.findViewById(R.id.widget_name);
            this.bottomHeader = (TextView) view.findViewById(R.id.widget_type);
            this.editIcon = (ImageView) view.findViewById(R.id.editIcon);
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

        customViewHolder.topHeader.setText(widget.getHumanName());
        customViewHolder.bottomHeader.setText("Some specific identifying detail");

        customViewHolder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new WidgetSettings();

                Bundle bundle = new Bundle();
                bundle.putInt(Widget.ID, widget.id);
                bundle.putInt(Widget.TYPE, widget.type);
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