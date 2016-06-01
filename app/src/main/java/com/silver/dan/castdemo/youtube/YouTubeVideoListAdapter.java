package com.silver.dan.castdemo.youtube;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.silver.dan.castdemo.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;

public class YouTubeVideoListAdapter extends RecyclerView.Adapter<YouTubeVideoListAdapter.ViewHolder> {

    private final OnVideoClickListener listItemClickListener;
    private List<YouTubeVideo> items;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.youtube_search_result_item, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final YouTubeVideo item = items.get(position);
        holder.nameTextView.setText(item.name);
        holder.tvChannelTitle.setText(item.channelTitle);


        DateFormat df = DateFormat.getDateInstance();
        String dateStr = df.format(item.publishedDate);
        holder.tvPublishedDate.setText(dateStr);

        Picasso.with(holder.ivThumbnail.getContext())
                .load(item.imageUrl)
                .into(holder.ivThumbnail);


        holder.vSearchResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemClickListener.onClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public YouTubeVideoListAdapter(List<YouTubeVideo> results, OnVideoClickListener clickListener) {
        this.items = results;
        this.listItemClickListener = clickListener;
    }


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public TextView tvChannelTitle;
        public TextView tvPublishedDate;
        public ImageView ivThumbnail;
        public View vSearchResult;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.video_name);
            tvChannelTitle = (TextView) itemView.findViewById(R.id.channel_title);
            tvPublishedDate = (TextView) itemView.findViewById(R.id.video_published_date);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.video_image);
            vSearchResult = itemView.findViewById(R.id.search_result);

        }
    }
}
