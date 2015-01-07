package com.worldventures.dreamtrips.view.adapter.item;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public interface ItemWrapper {

    RecyclerView.ViewHolder getBaseRecycleItem(ViewGroup parent);

    void bindViewHolder(RecyclerView.ViewHolder holder, int position);

    int getItemViewType();
}
