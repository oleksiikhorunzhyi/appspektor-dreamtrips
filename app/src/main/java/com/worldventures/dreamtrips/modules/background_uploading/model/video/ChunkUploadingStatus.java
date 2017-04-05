package com.worldventures.dreamtrips.modules.background_uploading.model.video;

import org.immutables.value.Value;

@Value.Immutable
public interface ChunkUploadingStatus {

   int chunkNumber();

   String eTag();
}