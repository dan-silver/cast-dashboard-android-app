package com.silvr.dan.castdemo.settingsFragments;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.silvr.dan.castdemo.CalendarInfo;
import com.silvr.dan.castdemo.CastCommunicator;
import com.silvr.dan.castdemo.R;
import com.silvr.dan.castdemo.Widget;
import com.silvr.dan.castdemo.WidgetOption;
import com.silvr.dan.castdemo.WidgetOption_Table;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Delete;

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
            this.calendarColor = (View) v.findViewById(R.id.calendar_color);
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
                if (isChecked) {

                    // add a CALENDAR_ENABLED entry
                    WidgetOption calendarEnabled = new WidgetOption();
                    calendarEnabled.key = CalendarSettings.CALENDAR_ENABLED;
                    calendarEnabled.value = calendar.id;
                    calendarEnabled.associateWidget(widget);
                    calendarEnabled.save();

                } else {
                    // remove the CALENDAR_ENABLED entry
                    ConditionGroup conditions = ConditionGroup.clause();
                    conditions.andAll(
                            WidgetOption_Table.widgetForeignKeyContainer_id.is(widget.id),
                            WidgetOption_Table.key.is(CalendarSettings.CALENDAR_ENABLED),
                            WidgetOption_Table.value.is(calendar.id));

                    new Delete()
                            .from(WidgetOption.class)
                            .where(conditions)
                            .execute();

                }
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
