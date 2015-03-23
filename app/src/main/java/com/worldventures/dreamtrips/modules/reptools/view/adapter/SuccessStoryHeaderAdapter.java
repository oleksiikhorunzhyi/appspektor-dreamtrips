package com.worldventures.dreamtrips.modules.reptools.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class  SuccessStoryHeaderAdapter implements StickyHeadersAdapter<SuccessStoryHeaderAdapter.SuccessHeaderViewHolder> {

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
        headerViewHolder.letter.setText(items.get(i).getCategory().toUpperCase());
    }

    @Override
    public long getHeaderId(int i) {
        String category = items.get(i).getCategory();
        int index = 0;
        for (int i1 = 0; i1 < category.length(); i1++) {
            index += category.charAt(i1);
        }
        return index;
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
