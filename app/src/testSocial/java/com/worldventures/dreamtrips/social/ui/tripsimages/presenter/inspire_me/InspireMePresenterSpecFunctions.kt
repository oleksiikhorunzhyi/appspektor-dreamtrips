package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.inspire_me

import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand

var instanceCount = 0

internal fun stubPhotos(size: Int = 1): ArrayList<Inspiration> {
   val list = ArrayList<Inspiration>()
   for (i in 1..size) {
      list.add(stubPhoto(i))
   }
   return list
}

internal fun stubPhoto(id: Int = 1) = Inspiration((++instanceCount).toString(),
      "url $id", "title $id", "author $id")

internal fun stubPhotosForLastPage() =
      stubPhotos(size = 1)

internal fun stubPhotosForNotLastPage() =
      stubPhotos(size = GetYSBHPhotosCommand.PER_PAGE)
