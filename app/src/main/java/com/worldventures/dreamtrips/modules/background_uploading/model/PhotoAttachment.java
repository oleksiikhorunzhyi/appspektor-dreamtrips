package com.worldventures.dreamtrips.modules.background_uploading.model;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.feed.model.SelectedPhoto;

import org.immutables.value.Value;

@Value.Immutable
public interface PhotoAttachment {

   int id();
   State state();
   int progress();
   @Nullable String originUrl();

   SelectedPhoto selectedPhoto();

   enum State {
      SCHEDULED, STARTED, UPLOADED, FAILED
   }
}
