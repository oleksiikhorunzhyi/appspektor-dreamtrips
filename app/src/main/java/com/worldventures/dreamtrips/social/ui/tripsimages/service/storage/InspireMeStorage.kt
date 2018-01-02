package com.worldventures.dreamtrips.social.ui.tripsimages.service.storage

import com.worldventures.core.janet.cache.CacheBundle
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.core.janet.cache.storage.MemoryStorage
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetInspireMePhotosCommand

class InspireMeStorage : ActionStorage<List<Inspiration>> {

   private val storage = MemoryStorage<List<Inspiration>>()

   override fun getActionClass(): Class<out CachedAction<*>> = GetInspireMePhotosCommand::class.java

   override fun save(params: CacheBundle?, data: List<Inspiration>) {
      if (params == null) {
         return
      }
      if (params.get(RELOAD)) {
         storage.save(params, ArrayList(data))
      } else if (params.get(LOAD_MORE)) {
         val cachedItems = fetchCache(params)
         cachedItems.addAll(data)
         storage.save(params, cachedItems)
      }
   }

   override fun get(params: CacheBundle?): List<Inspiration> = fetchCache(params)

   private fun fetchCache(params: CacheBundle?): MutableList<Inspiration> {
      val cachedItems = storage.get(params)
      return if (cachedItems != null) {
         ArrayList(cachedItems)
      } else {
         ArrayList()
      }
   }

   companion object {

      val RELOAD = "RELOAD"
      val LOAD_MORE = "LOAD_MORE"
   }
}
