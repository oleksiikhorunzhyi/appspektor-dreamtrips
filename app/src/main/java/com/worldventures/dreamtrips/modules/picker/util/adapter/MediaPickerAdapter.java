package com.worldventures.dreamtrips.modules.picker.util.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.picker.model.BaseMediaPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.util.adapter.holder.BaseMediaPickerHolder;

import java.util.List;


public class MediaPickerAdapter<M extends BaseMediaPickerViewModel> extends RecyclerView.Adapter<BaseMediaPickerHolder> {

   protected List<M> items;
   private MediaPickerHolderFactory factory;

   public MediaPickerAdapter(List<M> items, MediaPickerHolderFactory holderTypeFactory) {
      this.items = items;
      this.factory = holderTypeFactory;
   }

   @Override
   public BaseMediaPickerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return this.factory.holder(parent, viewType);
   }

   @Override
   public void onBindViewHolder(BaseMediaPickerHolder holder, int position) {
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
