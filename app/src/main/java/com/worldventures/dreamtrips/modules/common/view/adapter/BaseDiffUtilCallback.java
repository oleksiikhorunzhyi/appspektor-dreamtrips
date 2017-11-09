package com.worldventures.dreamtrips.modules.common.view.adapter;

import android.support.v7.util.DiffUtil;

import java.util.List;

public class BaseDiffUtilCallback extends DiffUtil.Callback {

   private final List oldList;
   private final List newList;

   public BaseDiffUtilCallback(List oldList, List newList) {
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
      return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
   }

   @Override
   public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
      return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
   }
}
