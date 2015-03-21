package com.worldventures.dreamtrips.modules.common.view.adapter;

import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.modules.common.view.adapter.item.ItemWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BaseRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<ItemWrapper> data = new ArrayList<>();
    Map<Integer, Integer> viewTypePositionMap = new ArrayMap<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return this.data.get(this.viewTypePositionMap.get(viewType)).getBaseRecycleItem(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        this.data.get(position).bindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    @Override
    public int getItemViewType(int position) {
        int itemViewType = this.data.get(position).getItemViewType();
        this.viewTypePositionMap.put(itemViewType, position);
        return itemViewType;
    }

    public void addItems(Collection<? extends ItemWrapper> itemWrappers) {
        this.data.addAll(itemWrappers);
        this.notifyDataSetChanged();
    }

    public void clear() {
        this.data.clear();
    }

    public ItemWrapper getItem(int pos) {
        return this.data.get(pos);
    }
}
