package com.worldventures.dreamtrips.modules.video.cell;

public interface ProgressVideoButtonActions<T> {

      void onDownloadVideo(T entity);

      void onDeleteVideo(T entity);

      void onCancelCachingVideo(T entity);
   }