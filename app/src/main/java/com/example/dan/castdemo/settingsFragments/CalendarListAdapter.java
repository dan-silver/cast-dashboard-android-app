package com.example.dan.castdemo.settingsFragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.dan.castdemo.CalendarInfo;
import com.example.dan.castdemo.R;

import java.util.ArrayList;
import java.util.List;

public class CalendarListAdapter extends RecyclerView.Adapter<CalendarListAdapter.CalendarViewHolder> {
    private List<CalendarInfo> calendars = new ArrayList<>();

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        protected TextView calendarName;
        protected CheckBox calendarEnabled;

        public CalendarViewHolder(View v) {
            super(v);
            this.calendarName = (TextView) v.findViewById(R.id.calendar_name);
            this.calendarEnabled = (CheckBox) v.findViewById(R.id.calendar_enabled);
        }
    }

    public CalendarListAdapter(List<CalendarInfo> myDataset) {
        calendars.addAll(myDataset);
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.calendar_select, viewGroup, false);

        return new CalendarViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CalendarViewHolder holder, int position) {
        holder.calendarName.setText(calendars.get(position).name);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return calendars.size();
    }
}
