package com.worldventures.dreamtrips.modules.video.cell;

import com.worldventures.dreamtrips.modules.video.model.CachedModel;

public interface ProgressVideoButtonActions {

      void onDownloadVideo(CachedModel entity);

      void onDeleteVideo(CachedModel entity);

      void onCancelCachingVideo(CachedModel entity);
   }