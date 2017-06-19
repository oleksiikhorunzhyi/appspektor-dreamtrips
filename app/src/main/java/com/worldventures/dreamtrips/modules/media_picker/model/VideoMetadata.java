package com.worldventures.dreamtrips.modules.media_picker.model;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public interface VideoMetadata {

   @Nullable String title();
   long duration();
   int width();
   int height();
}
