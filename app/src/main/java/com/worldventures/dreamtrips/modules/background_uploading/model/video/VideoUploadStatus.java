package com.worldventures.dreamtrips.modules.background_uploading.model.video;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface VideoUploadStatus {

   int chunkPosition();

   VideoUploadUrls registeredUrls();

   List<ChunkUploadingStatus> chunkStatuses();

}
