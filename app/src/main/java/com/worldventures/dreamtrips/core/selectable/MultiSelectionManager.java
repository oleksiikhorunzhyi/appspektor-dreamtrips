package com.worldventures.dreamtrips.core.selectable;

import android.support.v7.widget.RecyclerView;

import com.innahema.collections.query.queriables.Queryable;

import java.util.Collections;
import java.util.List;

public class MultiSelectionManager extends SimpleSelectionManager {

   public MultiSelectionManager(RecyclerView recyclerView) {
      super(recyclerView);
   }

   @Override
   protected void toggleSelectionImpl(int position) {
      selectableWrapperAdapter.toggleSelection(position);
   }

   @Override
   public void toggleSelection(int position) {
      super.toggleSelection(position);
      selectableWrapperAdapter.notifyItemChanged(position);
   }

   public List<Integer> getSelectedPositions() {
      return selectableWrapperAdapter.getSelectedItems();
   }

   public boolean isAllSelected() {
      return selectableWrapperAdapter.getSelectedItemCount() == selectableWrapperAdapter.getItemCount();
   }

   public boolean isAllSelected(int itemViewTypeId) {
      if (selectableWrapperAdapter.getItemCount() == 0) return false;

      return Queryable.from(selectableWrapperAdapter.getSelectedItems())
            .filter(pos -> (Integer) pos > 0 && selectableWrapperAdapter.getItemViewType((Integer) pos) == itemViewTypeId)
            .count() == Queryable.range(0, selectableWrapperAdapter.getItemCount())
            .filter(pos -> selectableWrapperAdapter.getItemViewType(pos) == itemViewTypeId)
            .count();
   }

   /**
    * Obtain list of selected positions for items of provided viewType only.
    *
    * @param itemViewTypeId viewType to query
    * @return list of positions of selected items
    */
   public List<Integer> getSelectedPositions(int itemViewTypeId) {
      if (selectableWrapperAdapter.getItemCount() == 0) return Collections.emptyList();

      return Queryable.from(selectableWrapperAdapter.getSelectedItems())
            .filter(pos -> selectableWrapperAdapter.getItemViewType((Integer) pos) == itemViewTypeId)
            .toList();
   }

   public void setSelectedPositions(List<Integer> selectionPositions) {
      selectableWrapperAdapter.clearSelections();
      for (Integer position : selectionPositions) {
         toggleSelectionImpl(position);
      }
      selectableWrapperAdapter.notifyDataSetChanged();
   }

   /**
    * Set selection for all elements.
    *
    * @param setSelected true if items need to be selected
    */
   public void setSelectionForAll(boolean setSelected) {
      selectableWrapperAdapter.clearSelections();
      if (setSelected) {
         for (int i = 0; i < selectableWrapperAdapter.getItemCount(); i++)
            toggleSelectionImpl(i);
      }
      selectableWrapperAdapter.notifyDataSetChanged();
   }
}
