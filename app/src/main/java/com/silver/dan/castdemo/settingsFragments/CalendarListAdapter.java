package com.silver.dan.castdemo.settingsFragments;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.silver.dan.castdemo.CalendarInfo;
import com.silver.dan.castdemo.CastCommunicator;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.WidgetOption;
import com.silver.dan.castdemo.WidgetOption_Table;

import java.util.ArrayList;
import java.util.List;

public class CalendarListAdapter extends RecyclerView.Adapter<CalendarListAdapter.CalendarViewHolder> {
    private final Widget widget;
    private List<CalendarInfo> calendars = new ArrayList<>();

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        protected TextView calendarName;
        protected CheckBox calendarEnabled;
        protected View calendarColor;

        public CalendarViewHolder(View v) {
            super(v);
            this.calendarName = (TextView) v.findViewById(R.id.calendar_name);
            this.calendarEnabled = (CheckBox) v.findViewById(R.id.calendar_enabled);
            this.calendarColor = v.findViewById(R.id.calendar_color);
        }
    }

    public CalendarListAdapter(List<CalendarInfo> myDataset, Widget widget) {
        calendars.addAll(myDataset);
        this.widget = widget;
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.calendar_select, viewGroup, false);

        return new CalendarViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final CalendarViewHolder holder, int position) {
        final CalendarInfo calendar = calendars.get(position);

        holder.calendarName.setText(calendar.name);
        holder.calendarColor.setBackgroundColor(Color.parseColor("#" + calendar.hexColor));
        holder.calendarEnabled.setOnCheckedChangeListener(null);
        holder.calendarEnabled.setChecked(calendar.enabled);

        holder.calendarEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calendar.enabled = isChecked;

                WidgetOption calendarEnabled = widget.getOption(CalendarSettings.CALENDAR_ENABLED);
                List<String> enabledIds = calendarEnabled.getList();



                if (isChecked) {
                    if (enabledIds.contains(calendar.id)) {
                        // already there
                    } else {
                        enabledIds.add(calendar.id);
                    }
                } else {
                    enabledIds.remove(calendar.id);
                }

                calendarEnabled.update(enabledIds);
                CastCommunicator.sendWidget(widget);

            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return calendars.size();
    }
}
