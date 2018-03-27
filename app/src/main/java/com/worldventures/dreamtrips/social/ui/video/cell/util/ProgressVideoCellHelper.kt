package com.worldventures.dreamtrips.social.ui.video.cell.util

import com.worldventures.core.model.CachedModel
import com.worldventures.core.modules.video.cell.ProgressMediaButtonActions
import com.worldventures.core.modules.video.model.Status.FAILED
import com.worldventures.core.modules.video.utils.CachedModelHelper
import com.worldventures.core.ui.view.custom.PinProgressButton

private const val MIN_PROGRESS = 10

class ProgressVideoCellHelper(private val pinProgressButton: PinProgressButton, private val cachedModelHelper: CachedModelHelper) {

   private lateinit var cacheModel: CachedModel

   fun setModelObject(cacheEntity: CachedModel) {
      this.cacheModel = cacheEntity
   }

   fun updateButtonState() {
      pinProgressButton.progress = cacheModel.progress
      when (cacheModel.cacheStatus == FAILED) {
         true -> setFailedState()
         false -> setProgressState()
      }
   }

   private fun setFailedState() {
      pinProgressButton.setFailed(true)
   }

   private fun setProgressState() {
      pinProgressButton.setFailed(false)

      var progress = cacheModel.progress
      when (cachedModelHelper.isCached(cacheModel)) {
         true -> progress = 100
      // put this check as 0 indicates on data level that entity is NOT downloading
         false -> if (progress > 0) progress = Math.max(MIN_PROGRESS, progress)
      }
      pinProgressButton.progress = progress
   }

   fun <T> onDownloadClick(delegate: ProgressMediaButtonActions<T>, entity: T) {
      val cached = cachedModelHelper.isCached(cacheModel)
      val inProgress = cacheModel.progress > 0 && cacheModel.progress < 100
      val failed = cacheModel.cacheStatus == FAILED

      if (!cached && !inProgress || failed) {
         delegate.onDownloadMedia(entity)
      } else if (cached) {
         delegate.onDeleteMedia(entity)
      } else {
         delegate.onCancelCachingMedia(entity)
      }
   }
}
