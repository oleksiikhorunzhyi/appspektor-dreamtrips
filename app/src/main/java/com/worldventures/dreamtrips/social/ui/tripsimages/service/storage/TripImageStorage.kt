package com.worldventures.dreamtrips.social.ui.tripsimages.service.storage

import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetMemberMediaCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetUsersMediaCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.MemberImagesAddedCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.MemberImagesRemovedCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.UserImagesRemovedCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs
import com.worldventures.janet.cache.CacheBundle
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.cache.storage.ClearableStorage
import com.worldventures.janet.cache.storage.MemoryStorage
import java.util.ArrayList
import java.util.Arrays
import java.util.concurrent.ConcurrentHashMap

class TripImageStorage : MultipleActionStorage<List<BaseMediaEntity<*>>>, ClearableStorage {

   private val map = ConcurrentHashMap<TripImagesArgs, MemoryStorage<List<BaseMediaEntity<*>>>>()

   override fun clearMemory() {
      for (memoryStorage in map.values) {
         memoryStorage.clearMemory()
      }
      map.clear()
   }

   override fun save(params: CacheBundle?, data: List<BaseMediaEntity<*>>) {
      if (params == null) {
         return
      }
      val args = params.get<TripImagesArgs>(PARAM_ARGS)
      var storage: MemoryStorage<List<BaseMediaEntity<*>>>? = map[args]
      if (storage == null) {
         storage = MemoryStorage()
         map.put(args, storage)
      }

      if (params.get(RELOAD)) {
         storage.save(params, ArrayList<BaseMediaEntity<*>>(data))
      } else if (params.get(LOAD_MORE)) {
         val cachedItems = fetchCache(storage, params)
         cachedItems.addAll(data)
         storage.save(params, cachedItems)
      } else if (params.get(LOAD_LATEST)) {
         val cachedItems = fetchCache(storage, params)
         cachedItems.addAll(0, data)
         storage.save(params, cachedItems)
      } else if (params.get(REMOVE_ITEMS)) {
         val cachedItems = fetchCache(storage, params)
         cachedItems.removeAll(data)
         storage.save(params, cachedItems)
      }
   }

   private fun fetchCache(storage: MemoryStorage<List<BaseMediaEntity<*>>>, params: CacheBundle?): MutableList<BaseMediaEntity<*>> {
      val cachedItems = storage.get(params)
      return if (cachedItems != null) {
         ArrayList(cachedItems)
      } else {
         ArrayList()
      }
   }

   override fun get(params: CacheBundle?): List<BaseMediaEntity<*>> {
      if (params == null) return emptyList()
      val args = params.get<TripImagesArgs>(PARAM_ARGS)
      var storage: MemoryStorage<List<BaseMediaEntity<*>>>? = map[args]
      if (storage == null) {
         storage = MemoryStorage()
         map.put(args, storage)
      }

      return fetchCache(storage, params)
   }

   override fun getActionClasses(): List<Class<out CachedAction<*>>> {
      return Arrays.asList(GetMemberMediaCommand::class.java, GetUsersMediaCommand::class.java,
            MemberImagesAddedCommand::class.java, MemberImagesRemovedCommand::class.java, UserImagesRemovedCommand::class.java)
   }

   companion object {

      val PARAM_ARGS = "args"
      val RELOAD = "RELOAD"
      val LOAD_MORE = "LOAD_MORE"
      val REMOVE_ITEMS = "REMOVE_ITEM"
      val LOAD_LATEST = "LOAD_LATEST"
   }
}
