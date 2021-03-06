package com.worldventures.wallet.ui.common.adapter;


import android.support.v7.util.DiffUtil;

import java.util.List;

public class SimpleDiffCallBack<T extends BaseViewModel> extends DiffUtil.Callback {

   private final List<T> oldList;
   private final List<T> newList;

   public SimpleDiffCallBack(List<T> oldList, List<T> newList) {
      this.oldList = oldList;
      this.newList = newList;
   }

   @Override
   public int getOldListSize() {
      return oldList.size();
   }

   @Override
   public int getNewListSize() {
      return newList.size();
   }

   @Override
   public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
      return oldList.get(oldItemPosition).getClass() == newList.get(newItemPosition).getClass();
   }

   @Override
   public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
      return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
   }
}
