package com.worldventures.dreamtrips.modules.picker.model;

import android.net.Uri;


public abstract class FacebookMediaPickerViewModel<S> extends BaseMediaPickerViewModel {

   private Uri uri;

   public FacebookMediaPickerViewModel(S source) {
      this.uri = getUriFromSource(source);
   }

   @Override
   public Uri getUri() {
      return uri;
   }

   public abstract Uri getUriFromSource(S source);
}
