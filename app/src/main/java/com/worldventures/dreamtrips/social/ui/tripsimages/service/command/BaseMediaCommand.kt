package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs

abstract class BaseMediaCommand(val args: TripImagesArgs) : CommandWithError<List<BaseMediaEntity<*>>>() {
   var isReload: Boolean = false
   var isLoadMore: Boolean = false
   var fromCacheOnly: Boolean = false
   protected lateinit var cachedItems: List<BaseMediaEntity<*>>
   val items: List<BaseMediaEntity<*>>
      get() {
         if (result != null) return result
         return cachedItems
      }

   abstract fun lastPageReached(): Boolean
}
