package com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public class MultiHolderAdapter<T extends BaseViewModel> extends RecyclerView.Adapter<BaseHolder> {

   private List<T> items;
   private HolderTypeFactory factory;

   public MultiHolderAdapter(List<T> items) {
      this.items = items;
      this.factory = new HolderFactoryImpl();
   }

   @Override
   public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return this.factory.holder(parent, viewType);
   }

   @Override
   public void onBindViewHolder(BaseHolder holder, int position) {
      holder.setData(items.get(position));
   }

   @Override
   public int getItemCount() {
      return items.size();
   }

   @Override
   public int getItemViewType(int position) {
      return items.get(position).type(this.factory);
   }

   public void updateItems(List<T> items) {
      this.items = items;
      notifyDataSetChanged();
   }
}
