package com.worldventures.dreamtrips.modules.background_uploading.model;

import com.worldventures.dreamtrips.modules.feed.model.SelectedPhoto;

import org.immutables.value.Value;

@Value.Immutable
public interface PhotoAttachment {

   State state();
   String originUrl();
   int progress();

   SelectedPhoto selectedPhoto();

   public enum State {
      SCHEDULED, STARTED, UPLOADED, FAILED
   }
}
