package com.worldventures.wallet.ui.common.adapter;

import java.util.List;

public class SimpleMultiHolderAdapter<ITEM extends BaseViewModel> extends MultiHolderAdapter<ITEM> {

   public SimpleMultiHolderAdapter(List<ITEM> items, HolderTypeFactory holderTypeFactory) {
      super(items, holderTypeFactory);
   }

   public void clearWithoutFirst() {
      if (items != null && !items.isEmpty()) {
         final ITEM item = items.get(0);
         items.clear();
         items.add(item);
         notifyDataSetChanged();
      }
   }
}
