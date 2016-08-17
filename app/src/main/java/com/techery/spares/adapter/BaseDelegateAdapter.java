package com.techery.spares.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.techery.spares.ui.view.cell.CellIdDelegate;

public class BaseDelegateAdapter<BaseItemClass> extends BaseArrayListAdapter<BaseItemClass> {

   private final SparseArray<CellDelegate> itemDelegateMapping = new SparseArray<>();
   private final SparseArray<CellIdDelegate> itemIdDelegateMapping = new SparseArray<>();

   public BaseDelegateAdapter(Context context, Injector injector) {
      super(context, injector);
   }

   /**
    * Register CellDelegate after {@link #registerCell(Class, Class) registerCell} method
    *
    * @param itemClass    Class of CellDelegate
    * @param cellDelegate CellDelegate implementation
    */
   public void registerDelegate(Class<?> itemClass, CellDelegate<? extends BaseItemClass> cellDelegate) {
      int index = viewTypes.indexOf(itemClass);
      if (index < 0) throw new IllegalStateException(itemClass.getSimpleName() + " is not registered as Cell");
      this.itemDelegateMapping.put(index, cellDelegate);
   }

   public <U> void registerIdDelegate(Class<U> itemClass, CellIdDelegate<U> cellDelegate) {
      int index = viewTypes.indexOf(itemClass);
      if (index < 0)
         throw new IllegalStateException(itemClass.getSimpleName() + " is not registered as Cell");
      this.itemIdDelegateMapping.put(index, cellDelegate);
      setHasStableIds(true);
   }

   @Override
   public long getItemId(int position) {
      int viewType = getItemViewType(position);
      CellIdDelegate idDelegate = itemIdDelegateMapping.get(viewType);
      if (idDelegate != null) return idDelegate.getId(getItem(position));
      else return super.getItemId(position);
   }

   @Override
   public AbstractCell onCreateViewHolder(ViewGroup parent, int viewType) {
      AbstractCell cell = super.onCreateViewHolder(parent, viewType);
      if (cell instanceof AbstractDelegateCell) {
         ((AbstractDelegateCell) cell).setCellDelegate(itemDelegateMapping.get(viewType));
      }
      return cell;
   }
}
