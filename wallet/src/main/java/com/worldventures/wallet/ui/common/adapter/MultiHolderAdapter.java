package com.worldventures.wallet.ui.common.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public abstract class MultiHolderAdapter<ITEM extends BaseViewModel> extends RecyclerView.Adapter<BaseHolder> {

   protected List<ITEM> items;
   private HolderTypeFactory factory;

   public MultiHolderAdapter(List<ITEM> items, HolderTypeFactory holderTypeFactory) {
      this.items = items;
      this.factory = holderTypeFactory;
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

   public void addItem(ITEM item) {
      int position = items.size();
      items.add(item);
      notifyItemInserted(position);
   }

   public void addItem(int position, ITEM item) {
      items.add(position, item);
      notifyItemInserted(position);
   }

   public void addItems(List<ITEM> items) {
      if (items != null) {
         int position = this.items.size();
         this.items.addAll(items);
         notifyItemRangeInserted(position, items.size());
      }
   }

   public void addItems(int position, List<ITEM> items) {
      if (items != null) {
         this.items.addAll(position, items);
         notifyItemRangeInserted(position, items.size());
      }
   }

   public void moveItem(int fromPosition, int toPosition) {
      if (fromPosition == toPosition) {
         return;
      }
      final ITEM item = items.remove(fromPosition);
      items.add(toPosition, item);
      notifyItemMoved(fromPosition, toPosition);
   }

   public void replaceItem(int position, ITEM item) {
      items.set(position, item);
      notifyItemChanged(position);
   }

   public void remove(ITEM item) {
      if (item != null) {
         int position = items.indexOf(item);
         if (position != -1) {
            remove(position);
         }
         notifyItemRemoved(position);
      }
   }

   public void remove(int position) {
      if (items.size() > position) {
         items.remove(position);
         notifyItemRemoved(position);
      }
   }

   public void clear() {
      if (items != null) {
         items.clear();
         notifyDataSetChanged();
      }
   }

   public List<ITEM> getItems() {
      return items;
   }
}
