package com.silver.dan.castdemo.settingsFragments;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.silver.dan.castdemo.CastCommunicator;
import com.silver.dan.castdemo.MainActivity;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.WidgetOption;
import com.silver.dan.castdemo.widgets.GoogleCalendarWidget;

import java.util.ArrayList;
import java.util.List;

public class GoogleCalendarListAdapter extends RecyclerView.Adapter<GoogleCalendarListAdapter.CalendarViewHolder> {
    private final Widget widget;
    private List<GoogleCalendarWidget.Calendar> calendars = new ArrayList<>();

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        protected TextView calendarName;
        protected CheckBox calendarEnabled;
        protected View calendarColor;

        public CalendarViewHolder(View v) {
            super(v);
            this.calendarName = v.findViewById(R.id.calendar_name);
            this.calendarEnabled = v.findViewById(R.id.calendar_enabled);
            this.calendarColor = v.findViewById(R.id.calendar_color);
        }
    }

    public void addCalendars(List<GoogleCalendarWidget.Calendar> calendars) {
        this.calendars.addAll(calendars);
        notifyItemRangeInserted(0, calendars.size());
    }

    public GoogleCalendarListAdapter(Widget widget) {
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
        final GoogleCalendarWidget.Calendar calendar = calendars.get(position);

        holder.calendarName.setText(calendar.summary);
        holder.calendarColor.setBackgroundColor(Color.parseColor(calendar.backgroundColor));
        holder.calendarEnabled.setOnCheckedChangeListener(null);
        holder.calendarEnabled.setChecked(calendar.enabled);

        holder.calendarEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calendar.enabled = isChecked;

                WidgetOption calendarEnabled = widget.loadOrInitOption(GoogleCalendarSettings.GOOGLE_CALENDARS_ENABLED, buttonView.getContext());
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
                new CastCommunicator(MainActivity.mCastSession).sendWidget(widget, holder.calendarName.getContext());

            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return calendars.size();
    }
}
