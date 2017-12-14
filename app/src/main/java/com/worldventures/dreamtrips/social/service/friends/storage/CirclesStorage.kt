package com.worldventures.dreamtrips.social.service.friends.storage

import com.worldventures.core.janet.cache.CacheBundle
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.core.model.Circle
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetCirclesCommand

class CirclesStorage(private val db: SocialSnappyRepository) : ActionStorage<List<Circle>> {

   override fun getActionClass(): Class<out CachedAction<*>> = GetCirclesCommand::class.java

   override fun save(params: CacheBundle?, data: List<Circle>) = db.saveCircles(data)

   override fun get(action: CacheBundle?): List<Circle> = db.circles
}
