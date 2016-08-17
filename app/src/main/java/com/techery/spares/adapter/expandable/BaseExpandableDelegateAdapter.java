package com.techery.spares.adapter.expandable;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;

public class BaseExpandableDelegateAdapter extends BaseExpandableAdapter {

   private final SparseArray<CellDelegate> itemDelegateMapping = new SparseArray<>();

   public BaseExpandableDelegateAdapter(Context context, Injector injector) {
      super(context, injector);
   }

   /**
    * Register CellDelegate after {@link #registerCell(Class, Class) registerCell} method
    *
    * @param itemClass    Class of CellDelegate
    * @param cellDelegate CellDelegate implementation
    */
   public void registerDelegate(Class<?> itemClass, CellDelegate<?> cellDelegate) {
      int index = viewTypes.indexOf(itemClass);
      if (index < 0) throw new IllegalStateException(itemClass.getSimpleName() + " is not registered as Cell");
      this.itemDelegateMapping.put(index, cellDelegate);
   }

   @Override
   public AbstractCell onCreateViewHolder(ViewGroup parent, int viewType) {
      AbstractCell cell = super.onCreateViewHolder(parent, viewType);
      if (cell instanceof GroupDelegateCell) {
         ((GroupDelegateCell) cell).setCellDelegate(itemDelegateMapping.get(viewType));
      }
      if (cell instanceof AbstractDelegateCell) {
         ((AbstractDelegateCell) cell).setCellDelegate(itemDelegateMapping.get(viewType));
      }
      return cell;
   }
}
