package com.example.dan.castdemo;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

public class AutoCmpAdapter extends ArrayAdapter<String> implements Filterable {
    ArrayList<String> listData;

    protected Filter filter;
    protected ArrayList<String> items;
    protected ArrayList<String> res;

    String sWds[] = {"SIMPSON", "JONES"};

    public AutoCmpAdapter(Context context, int textViewResourceId, ArrayList<String> listData) {
        super(context, textViewResourceId, 0, listData);

        filter = new PhysFilter();
        res = new ArrayList<>();
        this.listData = listData;
    }

    public Filter getFilter() {
        return filter;
    }

    private class PhysFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults f = new FilterResults();
            res.clear();
            if (constraint != null) {
                ArrayList<String> res = new ArrayList<>();
                for (int x = 0; x < sWds.length; x++) {
                    if (sWds[x].toUpperCase().contains(constraint.toString().toUpperCase())) {
                        res.add(sWds[x]);
                    }
                }
                f.values = res;//.toArray();
                f.count = res.size();
            }
            return f;
        }


        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            if (results.count > 0) {
                Log.println(Log.INFO, "Results", "FOUND");
                listData.clear();
                listData.addAll((ArrayList<String>) results.values);
                notifyDataSetChanged();
            } else {
                Log.println(Log.INFO, "Results", "-");
                notifyDataSetInvalidated();
            }
        }
    }
}