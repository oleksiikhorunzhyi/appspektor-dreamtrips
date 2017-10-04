package com.worldventures.dreamtrips.social.ui.background_uploading.model;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.social.ui.feed.model.SelectedPhoto;

import org.immutables.value.Value;

@Value.Immutable
public interface PhotoAttachment {

   int id();
   PostBody.State state();
   int progress();
   @Nullable
   String originUrl();

   SelectedPhoto selectedPhoto();
}
