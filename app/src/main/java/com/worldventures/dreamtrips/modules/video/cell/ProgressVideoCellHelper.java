package com.worldventures.dreamtrips.modules.video.cell;

import com.worldventures.dreamtrips.modules.common.view.custom.PinProgressButton;
import com.worldventures.dreamtrips.modules.membership.view.cell.delegate.PodcastCellDelegate;
import com.worldventures.dreamtrips.modules.video.cell.delegate.VideoCellDelegate;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;

import static com.worldventures.dreamtrips.modules.video.model.Status.*;

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

   public void onDownloadClick(VideoCellDelegate delegate) {
      if (delegate == null) return;
      //
      boolean cached = cachedModelHelper.isCached(cacheModel);
      boolean inProgress = cacheModel.getProgress() > 0 && cacheModel.getProgress() < 100;
      boolean failed = cacheModel.getCacheStatus() == FAILED;
      if ((!cached && !inProgress) || failed) {
         delegate.onDownloadVideo(cacheModel);
      } else if (cached) {
         delegate.onDeleteVideo(cacheModel);
      } else {
         delegate.onCancelCachingVideo(cacheModel);
      }
   }

   public void onDownloadClick(PodcastCellDelegate delegate) {
      if (delegate == null) return;
      //
      boolean cached = cachedModelHelper.isCached(cacheModel);
      boolean inProgress = cacheModel.getCacheStatus() == IN_PROGRESS;
      boolean failed = cacheModel.getCacheStatus() == FAILED;
      if ((!cached && !inProgress) || failed) {
         delegate.onDownloadPodcast(cacheModel);
      } else if (cached) {
         delegate.onDeletePodcast(cacheModel);
      } else {
         delegate.onCancelCachingPodcast(cacheModel);
      }
   }
}
