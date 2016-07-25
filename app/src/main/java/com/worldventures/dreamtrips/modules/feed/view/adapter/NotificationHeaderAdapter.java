package com.worldventures.dreamtrips.modules.feed.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.techery.spares.adapter.HeaderItem;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NotificationHeaderAdapter<T> implements StickyHeadersAdapter<NotificationHeaderAdapter.NotificationHeaderViewHolder> {

    private List<T> items;
    private int layout;
    private final HeaderConverter<T> converter;

    public NotificationHeaderAdapter(List<T> items, int layout, HeaderConverter<T> converter) {
        this.items = items;
        this.layout = layout;
        this.converter = converter;
    }

    public void setItems(List newItems) {
        this.items.clear();
        this.items.addAll(newItems);
    }

    @Override
    public NotificationHeaderViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        return new NotificationHeaderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationHeaderViewHolder headerViewHolder, int i) {
        HeaderItem header = converter.createHeader(items.get(i));
        String headerTitle = header.getHeaderTitle();
        headerViewHolder.itemView.setVisibility(headerTitle == null ? View.GONE : View.VISIBLE);
        if (headerTitle != null) headerViewHolder.letter.setText(headerTitle.toUpperCase());
    }

    @Override
    public long getHeaderId(int i) {
        if (items.isEmpty() || items.size() < i) return RecyclerView.NO_ID;
        //
        HeaderItem header = converter.createHeader(items.get(i));
        String categoryTitle = header.getHeaderTitle();
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

    public interface HeaderConverter<T> {
        HeaderItem createHeader(T item);
    }

}
