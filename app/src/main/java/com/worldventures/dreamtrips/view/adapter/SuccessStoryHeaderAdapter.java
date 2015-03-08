package com.worldventures.dreamtrips.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.SuccessStory;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SuccessStoryHeaderAdapter implements StickyHeadersAdapter<SuccessStoryHeaderAdapter.SuccessHeaderViewHolder> {

    private List<SuccessStory> items;

    public SuccessStoryHeaderAdapter(List<SuccessStory> items) {

        this.items = items;
    }

    @Override
    public SuccessHeaderViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_item_succes_story_header, viewGroup, false);

        return new SuccessHeaderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SuccessHeaderViewHolder headerViewHolder, int i) {
        headerViewHolder.letter.setText(items.get(i).getCategory());
    }

    @Override
    public long getHeaderId(int i) {
        return items.get(i).getCategory().hashCode();
    }


    public static class SuccessHeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.tv_title)
        TextView letter;

        public SuccessHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
