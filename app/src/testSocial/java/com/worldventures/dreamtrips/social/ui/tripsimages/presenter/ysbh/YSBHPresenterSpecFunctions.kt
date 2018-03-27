package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh

import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand

var instanceCount = 0

internal fun stubPhotos(size: Int = 1): ArrayList<YSBHPhoto> {
   val list = ArrayList<YSBHPhoto>()
   for (i in 1..size) {
      list.add(stubPhoto(i))
   }
   return list
}

internal fun stubPhoto(id: Int = 1) = YSBHPhoto(++instanceCount, "url $id", "title $id")

internal fun stubPhotosForLastPage() =
      stubPhotos(size = 1)

internal fun stubPhotosForNotLastPage() =
      stubPhotos(size = GetYSBHPhotosCommand.PER_PAGE)
