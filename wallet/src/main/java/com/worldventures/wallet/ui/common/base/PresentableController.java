package com.worldventures.wallet.ui.common.base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.rxlifecycle.RxController;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

public abstract class PresentableController<V extends WalletScreen, P extends WalletPresenter> extends RxController {

   public PresentableController() {
      super();
   }

   public PresentableController(Bundle args) {
      super(args);
   }

   @NonNull
   @Override
   protected View onCreateView(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
      return createView(layoutInflater, viewGroup);
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      getPresenter().attachView((V) this);
   }


   @Override
   protected void onDetach(@NonNull View view) {
      super.onDetach(view);
      getPresenter().detachView(true);
   }


   public Context getContext() {
      return getActivity();
   }

   public abstract View createView(LayoutInflater layoutInflater, ViewGroup viewGroup);

   public abstract P getPresenter();
}
