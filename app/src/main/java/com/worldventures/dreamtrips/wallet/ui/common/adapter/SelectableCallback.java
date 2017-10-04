package com.worldventures.dreamtrips.wallet.ui.common.adapter;

public interface SelectableCallback {

   void toggleSelection(int position);

   void setSelection(int position, boolean isSelected);

   boolean isSelected(int position);
}
