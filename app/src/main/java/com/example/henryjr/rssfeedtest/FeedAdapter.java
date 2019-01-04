package com.example.henryjr.rssfeedtest;

import android.content.Intent;
import android.text.Html;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedModelViewHolder> {

    private List<FeedModel> items;
    private MainActivity mActivity;

    public class FeedModelViewHolder extends RecyclerView.ViewHolder {
        private View rssFeedView;
        private TextView textView;
        FeedModel item;

        private FeedModelViewHolder(View v) {
            super(v);
            rssFeedView = v;
            textView = v.findViewById(R.id.descriptionText);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, WebContentActivity.class);
                    intent.putExtra("content", item.description);
                    intent.putExtra("title", item.title);
                    intent.putExtra("link", item.link);
                    mActivity.startActivity(intent);
                }
            });
        }
    }

    public FeedAdapter(MainActivity mActivity, List<FeedModel> rssFeedModels) {
        items = rssFeedModels;
        this.mActivity = mActivity;
    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed, parent, false);
        FeedModelViewHolder holder = new FeedModelViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {
        final FeedModel item = items.get(position);
        holder.item = item;
        ((TextView) holder.rssFeedView.findViewById(R.id.titleText)).setText(item.title);

        ImageGetter imageGetter = new ImageGetter(mActivity, holder.textView);
        Spannable html;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            html = (Spannable) Html.fromHtml(item.description, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
        } else {
            html = (Spannable) Html.fromHtml(item.description, imageGetter, null);
        }
        holder.textView.setText(html);

    }


    @Override
    public int getItemCount() {
        return items.size();
    }
}
