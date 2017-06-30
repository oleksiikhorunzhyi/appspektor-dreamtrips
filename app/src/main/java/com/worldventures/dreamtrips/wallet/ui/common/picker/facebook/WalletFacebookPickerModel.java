package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook;

import android.net.Uri;

import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerViewModel;


public abstract class WalletFacebookPickerModel<S> extends BasePickerViewModel {

   private Uri uri;

   public WalletFacebookPickerModel(S source) {
      this.uri = getUriFromSource(source);
   }

   @Override
   public Uri getUri() {
      return uri;
   }

   public abstract Uri getUriFromSource(S source);
}
