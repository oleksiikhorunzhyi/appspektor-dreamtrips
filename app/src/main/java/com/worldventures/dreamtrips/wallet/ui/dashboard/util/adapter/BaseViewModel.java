package com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter;


import android.os.Parcelable;

public abstract class BaseViewModel<T extends HolderTypeFactory> implements Parcelable {

   protected String modelId;

   public abstract int type(T typeFactory);

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      BaseViewModel that = (BaseViewModel) o;

      return modelId.equals(that.modelId);
   }

   @Override
   public int hashCode() {
      return modelId.hashCode();
   }
}
