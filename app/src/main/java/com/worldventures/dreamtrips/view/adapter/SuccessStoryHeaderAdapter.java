package com.worldventures.dreamtrips.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SuccessStoryHeaderAdapter implements StickyHeadersAdapter<SuccessStoryHeaderAdapter.SuccessHeaderViewHolder> {

    @Override
    public SuccessHeaderViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_item_succes_story_header, viewGroup, false);

        return new SuccessHeaderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SuccessHeaderViewHolder headerViewHolder, int i) {
        headerViewHolder.letter.setText("JOPA");
    }

    @Override
    public long getHeaderId(int i) {
        return 0;
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
