package com.worldventures.dreamtrips.wallet.ui.common.viewstate;

import android.os.Parcelable;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

public abstract class WalletViewState<V extends MvpView> implements ViewState<V>, Parcelable {

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void apply(V view, boolean retained) {
   }
}
