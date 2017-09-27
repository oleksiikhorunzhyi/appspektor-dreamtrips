package com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter;


import android.support.v7.util.DiffUtil;

import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseViewModel;

import java.util.List;

public class CardDiffCallBack<T extends BaseViewModel> extends DiffUtil.Callback {

   private List<T> oldList;
   private List<T> newList;

   public CardDiffCallBack(List<T> oldList, List<T> newList) {
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
      return oldList.get(oldItemPosition)
            .equals(newList.get(newItemPosition));
   }
}
