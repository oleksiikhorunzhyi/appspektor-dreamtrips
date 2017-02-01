package com.worldventures.dreamtrips.core.selectable;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.utils.BaseWrapperAdapter;
import com.innahema.collections.query.queriables.Queryable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectableWrapperAdapter<VH extends RecyclerView.ViewHolder> extends BaseWrapperAdapter<VH> {

   private Set<Integer> selectedItems;
   private SelectableDelegateWrapper selectableDelegateWrapper;

   public SelectableWrapperAdapter(RecyclerView.Adapter<VH> adapter, SelectionManager selectionManager) {
      super(adapter);
      selectedItems = new HashSet<>();
      this.selectableDelegateWrapper = new SelectableDelegateWrapper(selectionManager);
   }

   @Override
   public VH onCreateViewHolder(ViewGroup parent, int viewType) {
      VH holder = super.onCreateViewHolder(parent, viewType);
      if (holder instanceof SelectableCell) {
         ((SelectableCell) holder).setSelectableDelegate(selectableDelegateWrapper);
      }
      //
      return holder;
   }

   @Override
   public void onBindViewHolder(VH holder, int position) {
      super.onBindViewHolder(holder, position);
   }

   public void toggleSelection(Integer pos) {
      if (selectedItems.contains(pos)) selectedItems.remove(pos);
      else selectedItems.add(pos);
   }

   public void setSelection(Integer pos, boolean isSelected) {
      if (isSelected) selectedItems.add(pos);
      else selectedItems.remove(pos);
   }

   public void clearSelections() {
      selectedItems.clear();
   }

   int getSelectedItemCount() {
      return selectedItems.size();
   }

   List<Integer> getSelectedItems() {
      return Queryable.from(selectedItems).toList();
   }

   private static class SelectableDelegateWrapper implements SelectableDelegate {

      private SelectionManager selectionManager;

      public SelectableDelegateWrapper(SelectionManager selectionManager) {
         this.selectionManager = selectionManager;
      }

      @Override
      public void setSelection(int position, boolean isSelected) {
         selectionManager.setSelection(position, isSelected);
      }

      @Override
      public void toggleSelection(int position) {
         selectionManager.toggleSelection(position);
      }

      @Override
      public boolean isSelected(int position) {
         return selectionManager.isSelected(position);
      }
   }

}
