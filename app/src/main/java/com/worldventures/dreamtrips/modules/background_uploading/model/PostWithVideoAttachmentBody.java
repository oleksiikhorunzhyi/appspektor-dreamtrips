package com.worldventures.dreamtrips.modules.background_uploading.model;


import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.background_uploading.model.video.VideoUploadStatus;

import org.immutables.value.Value;

@Value.Immutable
public interface PostWithVideoAttachmentBody extends PostBody {

   String videoPath();

   PostBody.State state();

   @Nullable
   String remoteURL();

   double aspectRatio();

   @Nullable
   VideoUploadStatus videoUploadStatus();
}
