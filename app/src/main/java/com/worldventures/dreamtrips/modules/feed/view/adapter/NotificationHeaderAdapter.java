package com.worldventures.dreamtrips.modules.feed.view.adapter;

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
        headerViewHolder.letter.setText(items.get(i).getHeaderTitle().toUpperCase());
        headerViewHolder.itemView.setVisibility( items.get(i).getHeaderTitle().contentEquals(HeaderItem.NON_SHOWING_HEADER_VALUE)?
                                                    View.GONE : View.VISIBLE);
    }

    @Override
    public long getHeaderId(int i) {
        String category = items.get(i).getHeaderTitle();
        int index = 0;
        for (int i1 = 0; i1 < category.length(); i1++) {
            index += category.charAt(i1);
        }
        return index;
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
        String NON_SHOWING_HEADER_VALUE = "";

        String getHeaderTitle();
    }
}
