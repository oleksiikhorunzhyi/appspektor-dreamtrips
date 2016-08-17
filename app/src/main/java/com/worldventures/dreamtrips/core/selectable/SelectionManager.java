package com.worldventures.dreamtrips.core.selectable;

import android.support.v7.widget.RecyclerView;

public interface SelectionManager {

   void toggleSelection(int position);

   void setSelection(int position, boolean isSelected);

   boolean isSelected(int position);

   RecyclerView.Adapter provideWrappedAdapter(RecyclerView.Adapter adapter);

   void setEnabled(boolean enabled);

   void release();
}
