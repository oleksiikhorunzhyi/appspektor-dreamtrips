package com.worldventures.dreamtrips.core.selectable;

import android.support.v7.widget.RecyclerView;

public class SingleSelectionManager extends SimpleSelectionManager {

   public SingleSelectionManager(RecyclerView recyclerView) {
      super(recyclerView);
   }

   @Override
   protected void setSelectionImpl(int pos, boolean isSelection) {
      selectableWrapperAdapter.setSelection(pos, isSelection);
      selectableWrapperAdapter.notifyItemChanged(pos);
   }

   @Override
   protected void toggleSelectionImpl(int position) {
      selectableWrapperAdapter.toggleSelection(position);
      selectableWrapperAdapter.notifyItemChanged(position);
   }

   public void clearSelections() {
      selectableWrapperAdapter.clearSelections();
      selectableWrapperAdapter.notifyDataSetChanged();
   }
}
