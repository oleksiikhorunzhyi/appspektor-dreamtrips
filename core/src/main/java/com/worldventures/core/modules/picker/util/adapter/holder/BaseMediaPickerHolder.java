package com.worldventures.core.modules.picker.util.adapter.holder;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

import com.worldventures.core.modules.picker.viewmodel.BaseMediaPickerViewModel;


public abstract class BaseMediaPickerHolder<B extends ViewDataBinding, M extends BaseMediaPickerViewModel> extends RecyclerView.ViewHolder {

   private final B dataBinding;

   public BaseMediaPickerHolder(B dataBinding) {
      super(dataBinding.getRoot());
      this.dataBinding = dataBinding;
   }

   public B getDataBinding() {
      return dataBinding;
   }


   public abstract void setData(M data);
}
