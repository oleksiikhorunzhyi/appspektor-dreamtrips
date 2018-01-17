package com.worldventures.dreamtrips.social.ui.tripsimages.service.storage

import com.worldventures.core.janet.cache.CacheBundle
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.core.janet.cache.storage.MemoryStorage
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand

class YsbhPhotoStorage : ActionStorage<List<YSBHPhoto>> {

   private val storage = MemoryStorage<List<YSBHPhoto>>()

   override fun getActionClass(): Class<out CachedAction<*>> {
      return GetYSBHPhotosCommand::class.java
   }

   override fun save(params: CacheBundle?, data: List<YSBHPhoto>) {
      if (params != null) {
         if (params.get(RELOAD)) {
            storage.save(params, ArrayList(data))
         } else if (params.get(LOAD_MORE)) {
            val cachedItems = fetchCache(params)
            cachedItems.addAll(data)
            storage.save(params, cachedItems)
         }
      }
   }

   override fun get(params: CacheBundle?) = fetchCache(params)

   private fun fetchCache(params: CacheBundle?): MutableList<YSBHPhoto> {
      val cachedItems = storage.get(params)
      return if (cachedItems != null) {
         ArrayList(cachedItems)
      } else ArrayList()
   }

   companion object {

      val RELOAD = "RELOAD"
      val LOAD_MORE = "LOAD_MORE"
   }
}
