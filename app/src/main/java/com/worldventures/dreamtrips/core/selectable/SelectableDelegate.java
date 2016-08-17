package com.worldventures.dreamtrips.core.selectable;

public interface SelectableDelegate {

   void toggleSelection(int position);

   void setSelection(int position, boolean isSelected);

   boolean isSelected(int position);
}
