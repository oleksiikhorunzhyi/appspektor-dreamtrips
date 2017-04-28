package com.worldventures.dreamtrips.core.selectable;

import android.support.v7.widget.RecyclerView;

import java.util.List;

public class SimpleSelectionManager implements SelectionManager {

   protected SelectableWrapperAdapter selectableWrapperAdapter;
   protected boolean enabled = true;

   protected RecyclerView recyclerView;

   public SimpleSelectionManager(RecyclerView recyclerView) {
      this.recyclerView = recyclerView;
   }

   @Override
   public void toggleSelection(int position) {
      if (!enabled) return;
      //
      toggleSelectionImpl(position);
   }

   @Override
   public void setSelection(int position, boolean isSelected) {
      if (!enabled) return;
      //
      setSelectionImpl(position, isSelected);
   }

   protected void setSelectionImpl(int pos, boolean isSelection) {
      selectableWrapperAdapter.setSelection(pos, isSelection);
      selectableWrapperAdapter.notifyDataSetChanged();
   }

   protected void toggleSelectionImpl(int position) {
      selectableWrapperAdapter.toggleSelection(position);
      selectableWrapperAdapter.notifyDataSetChanged();
   }

   public int getSelectedPosition() {
      List<Integer> list = selectableWrapperAdapter.getSelectedItems();
      return list.isEmpty() ? -1 : list.get(0);
   }

   @Override
   public boolean isSelected(int position) {
      return enabled && selectableWrapperAdapter.getSelectedItems().contains(position);
   }

   @Override
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   @Override
   @SuppressWarnings("unchecked")
   public RecyclerView.Adapter provideWrappedAdapter(RecyclerView.Adapter adapter) {
      selectableWrapperAdapter = new SelectableWrapperAdapter(adapter, this);
      return selectableWrapperAdapter;
   }

   @Override
   public void release() {
      selectableWrapperAdapter = null;
      recyclerView = null;
   }
}
