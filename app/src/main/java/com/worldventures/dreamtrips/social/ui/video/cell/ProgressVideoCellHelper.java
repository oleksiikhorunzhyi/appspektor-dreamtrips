package com.worldventures.dreamtrips.social.ui.video.cell;

import com.worldventures.core.model.CachedModel;
import com.worldventures.core.modules.video.cell.ProgressMediaButtonActions;
import com.worldventures.core.modules.video.utils.CachedModelHelper;
import com.worldventures.core.ui.view.custom.PinProgressButton;

import static com.worldventures.core.modules.video.model.Status.FAILED;

public class ProgressVideoCellHelper {

   private static final int MIN_PROGRESS = 10;

   private final PinProgressButton pinProgressButton;
   private final CachedModelHelper cachedModelHelper;

   private CachedModel cacheModel;

   public ProgressVideoCellHelper(PinProgressButton pinProgressButton, CachedModelHelper cachedModelHelper) {
      this.pinProgressButton = pinProgressButton;
      this.cachedModelHelper = cachedModelHelper;
   }

   public void syncUIStateWithModel() {
      pinProgressButton.setProgress(cacheModel.getProgress());
      if (cacheModel.getCacheStatus() == FAILED) {
         setFailedState();
      } else {
         setInProgressState();
      }
   }

   private void setFailedState() {
      pinProgressButton.setFailed(true);
   }

   private void setInProgressState() {
      pinProgressButton.setFailed(false);
      if (cachedModelHelper.isCached(cacheModel)) {
         pinProgressButton.setProgress(100);
      } else {
         int progress = cacheModel.getProgress();
         // put this check as 0 indicates on data level that entity is NOT downloading
         if (progress > 0) {
            progress = Math.max(MIN_PROGRESS, progress);
         }
         pinProgressButton.setProgress(progress);
      }
   }

   public void setModelObject(CachedModel cacheEntity) {
      this.cacheModel = cacheEntity;
   }

   public <T> void onDownloadClick(ProgressMediaButtonActions<T> delegate, T entity) {
      if (delegate == null) {
         return;
      }
      boolean cached = cachedModelHelper.isCached(cacheModel);
      boolean inProgress = cacheModel.getProgress() > 0 && cacheModel.getProgress() < 100;
      boolean failed = cacheModel.getCacheStatus() == FAILED;
      if ((!cached && !inProgress) || failed) {
         delegate.onDownloadMedia(entity);
      } else if (cached) {
         delegate.onDeleteMedia(entity);
      } else {
         delegate.onCancelCachingMedia(entity);
      }
   }
}
