package com.worldventures.dreamtrips.modules.feed.view.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NotificationHeaderAdapter implements StickyHeadersAdapter<NotificationHeaderAdapter.NotificationHeaderViewHolder> {

    private List<? extends HeaderItem> items;
    private int layout;

    public NotificationHeaderAdapter(List<? extends HeaderItem> items, int layout) {
        this.items = items;
        this.layout = layout;
    }

    @Override
    public NotificationHeaderViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);

        return new NotificationHeaderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationHeaderViewHolder headerViewHolder, int i) {
        String headerTitle = items.get(i).getHeaderTitle();
        headerViewHolder.itemView.setVisibility(headerTitle == null ? View.GONE : View.VISIBLE);
        if (headerTitle != null) headerViewHolder.letter.setText(headerTitle.toUpperCase());
    }

    @Override
    public long getHeaderId(int i) {
        String categoryTitle = items.get(i).getHeaderTitle();
        int headerId = 0;

        if (categoryTitle == null) return headerId;

        for (int cursor = 0; cursor < categoryTitle.length(); cursor++) {
            headerId += categoryTitle.charAt(cursor);
        }
        return headerId;
    }


    public static class NotificationHeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.divider_title)
        protected TextView letter;

        public NotificationHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }


    public interface HeaderItem {

        @Nullable
        String getHeaderTitle();

    }
}
