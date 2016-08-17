package com.messenger.ui.module;

import android.os.Parcelable;
import android.view.View;

public abstract class ModuleStatefulViewImpl<P extends ModuleStatefulPresenter> extends ModuleViewImpl<P> implements ModuleStatefulView<P> {

   public ModuleStatefulViewImpl(View parentView) {
      super(parentView);
   }

   @Override
   public void onSaveInstanceState(Parcelable parcelable) {
      getPresenter().onSaveInstanceState(parcelable);
   }

   @Override
   public void onRestoreInstanceState(Parcelable parcelable) {
      getPresenter().onRestoreInstanceState(parcelable);
   }
}
