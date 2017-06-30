package com.worldventures.dreamtrips.wallet.ui.common.picker.base;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseHolder;

import java.util.List;


public class BaseWalletPickerAdapter<M extends BasePickerViewModel> extends RecyclerView.Adapter<BaseHolder> {

   protected List<M> items;
   private WalletPickerHolderFactory factory;

   public BaseWalletPickerAdapter(List<M> items, WalletPickerHolderFactory holderTypeFactory) {
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

   public void updateItems(List<M> items) {
      if (items != null) {
         int insertedAt = this.items.size();
         this.items.addAll(items);
         notifyItemRangeInserted(insertedAt, items.size());
      }
   }

   public void updateItem(int position) {
      final M model = getItem(position);
      model.setChecked(!model.isChecked());
      model.setPickedTime(model.isChecked() ? System.currentTimeMillis() : -1);
      notifyItemChanged(position);
   }

   public void updateItem(M model, int position) {
      items.set(position, model);
      notifyItemChanged(position);
   }

   public void clear() {
      int itemCount = items.size();
      items.clear();
      notifyItemRangeRemoved(0, itemCount);
   }

   public M getItem(int position) {
      return items.get(position);
   }

   public List<M> getItems() {
      return items;
   }

   public int getPositionFromItem(M item) {
      return items.indexOf(item);
   }

   public List<M> getChosenMedia(int staticItemsCount) {
      return Queryable.from(items.subList(staticItemsCount, items.size())).filter(M::isChecked).toList();
   }

   public List<M> getChosenMedia() {
      return getChosenMedia(0);
   }
}
