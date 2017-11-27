package com.worldventures.core.modules.video.cell;

public interface ProgressMediaButtonActions<T> {

   void onDownloadMedia(T entity);

   void onDeleteMedia(T entity);

   void onCancelCachingMedia(T entity);
}