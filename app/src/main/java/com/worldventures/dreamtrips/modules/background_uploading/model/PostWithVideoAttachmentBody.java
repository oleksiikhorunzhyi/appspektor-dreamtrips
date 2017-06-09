package com.worldventures.dreamtrips.modules.background_uploading.model;


import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public interface PostWithVideoAttachmentBody extends PostBody {

   String videoPath();

   PostBody.State state();

   @Nullable
   String uploadId();

   @Nullable
   String videoUid();

   double aspectRatio();

   long size();
}
