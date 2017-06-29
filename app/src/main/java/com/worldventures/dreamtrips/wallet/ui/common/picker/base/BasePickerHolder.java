package com.worldventures.dreamtrips.wallet.ui.common.picker.base;

import android.databinding.ViewDataBinding;

import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseHolder;


public abstract class BasePickerHolder<B extends ViewDataBinding, M extends BasePickerViewModel> extends BaseHolder<M> {

   private final B dataBinding;

   public BasePickerHolder(B dataBinding) {
      super(dataBinding.getRoot());
      this.dataBinding = dataBinding;
   }

   public B getDataBinding() {
      return dataBinding;
   }
}
