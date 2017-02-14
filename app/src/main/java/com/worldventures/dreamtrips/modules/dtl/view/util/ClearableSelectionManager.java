package com.worldventures.dreamtrips.modules.dtl.view.util;


import android.support.v7.widget.RecyclerView;

import com.worldventures.dreamtrips.core.selectable.SimpleSelectionManager;

public class ClearableSelectionManager extends SimpleSelectionManager {

   public ClearableSelectionManager(RecyclerView recyclerView) {
      super(recyclerView);
   }

   public void clearSelections() {
      selectableWrapperAdapter.clearSelections();
      selectableWrapperAdapter.notifyDataSetChanged();
   }
}