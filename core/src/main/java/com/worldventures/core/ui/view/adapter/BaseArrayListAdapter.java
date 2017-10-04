package com.worldventures.core.ui.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.BaseEntity;
import com.worldventures.core.ui.view.cell.AbstractCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseArrayListAdapter<BaseItemClass> extends RecyclerView.Adapter<AbstractCell> implements ListAdapter<BaseItemClass> {

   private final Map<Class, Class<? extends AbstractCell>> itemCellMapping = new HashMap<>();

   private final AdapterHelper adapterHelper;
   protected final Injector injector;
   protected List<BaseItemClass> items = new ArrayList<>();

   protected List<Class> viewTypes = new ArrayList<>();

   public BaseArrayListAdapter(Context context, Injector injector) {
      this.injector = injector;
      this.adapterHelper = new AdapterHelper((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
   }

   public void registerCell(Class<?> itemClass, Class<? extends AbstractCell> cellClass) {
      this.itemCellMapping.put(itemClass, cellClass);
      int type = this.viewTypes.indexOf(itemClass);
      if (type == -1) {
         this.viewTypes.add(itemClass);
      }
   }

   /**
    * Return itemViewType ID for items of given model class
    *
    * @param itemClass model object class to query
    * @return viewType id
    */
   public int getClassItemViewType(Class<?> itemClass) {
      int index = viewTypes.indexOf(itemClass);
      if (index < 0) {
         throw new IllegalArgumentException(itemClass.getSimpleName() + " is not registered");
      }
      return this.viewTypes.indexOf(itemClass);
   }

   @Override
   public AbstractCell onCreateViewHolder(ViewGroup parent, int viewType) {
      Class itemClass = this.viewTypes.get(viewType);
      Class<? extends AbstractCell> cellClass = this.itemCellMapping.get(itemClass);
      AbstractCell cell = this.adapterHelper.buildCell(cellClass, parent);
      if (cell.shouldInject()) {
         this.injector.inject(cell);
         cell.afterInject();
      }
      return cell;
   }

   @Override
   public long getItemId(int position) {
      if (getItem(position) instanceof BaseEntity) {
         return ((BaseEntity) getItem(position)).getId();
      }
      return super.getItemId(position);
   }

   @Override
   public int getItemViewType(int position) {
      BaseItemClass baseItemClass = this.items.get(position);
      Class itemClass = baseItemClass.getClass();
      return getClassItemViewType(itemClass);
   }

   @Override
   public void onBindViewHolder(AbstractCell cell, int position) {
      BaseItemClass item = this.getItem(position);

      cell.prepareForReuse();
      cell.fillWithItem(item);
   }

   @Override
   public int getItemCount() {
      return this.items.size();
   }

   public int getItemCount(Class<?> itemClass) {
      int classViewType = getClassItemViewType(itemClass);
      return Queryable.from(getItems())
            .filter((element, index) -> getItemViewType(index) == classViewType)
            .count();
   }

   public BaseItemClass getItem(int position) {
      return this.items.get(position);
   }

   @Override
   public void addItems(List<BaseItemClass> items) {
      if (items != null) {
         int insertedAt = this.items.size();
         this.items.addAll(items);
         this.notifyItemRangeInserted(insertedAt, items.size());
      }
   }

   public void addItems(int index, List<BaseItemClass> result) {
      if (result != null) {
         this.items.addAll(index, result);
         this.notifyItemRangeInserted(index, result.size());
      }
   }

   public void addItem(int location, BaseItemClass obj) {
      this.items.add(location, obj);
   }

   public void addItem(BaseItemClass obj) {
      this.items.add(obj);
   }

   public void replaceItem(int location, BaseItemClass obj) {
      this.items.set(location, obj);
   }

   public void remove(int location) {
      if (items.size() > location) items.remove(location);
   }

   public void remove(BaseItemClass item) {
      if (item != null) {
         int position = items.indexOf(item);
         if (position != -1) {
            this.items.remove(position);
            this.notifyItemRemoved(position);
         }
      }
   }

   public void moveItem(int fromPosition, int toPosition) {
      if (fromPosition == toPosition) {
         return;
      }

      final BaseItemClass item = items.remove(fromPosition);

      items.add(toPosition, item);
   }

   public void clear() {
      this.items.clear();
      notifyDataSetChanged();
   }

   public void setItems(List<BaseItemClass> baseItemClasses) {
      this.items = baseItemClasses;
      this.notifyDataSetChanged();
   }

   public void setItemsNoNotify(List<BaseItemClass> baseItemClasses) {
      this.items = baseItemClasses;
   }

   public void updateItem(BaseItemClass changedItem) {
      Queryable.from(items).forEachR(item -> {
         if (!item.equals(changedItem)) return;
         int position = items.indexOf(item);
         items.set(position, changedItem);
         notifyItemChanged(position);
      });
   }

   public void clearAndUpdateItems(List<BaseItemClass> updatedItems) {
      items.clear();
      getItems().addAll(updatedItems);
      notifyDataSetChanged();
   }

   public List<BaseItemClass> getItems() {
      return items;
   }

   @Override
   public int getCount() {
      return getItemCount();
   }
}
