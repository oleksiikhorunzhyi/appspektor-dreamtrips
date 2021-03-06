package com.worldventures.dreamtrips.social.service.users.circle.storage

import com.worldventures.janet.cache.CacheBundle
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.cache.storage.ActionStorage
import com.worldventures.core.model.Circle
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesCommand

class CirclesStorage(private val db: SocialSnappyRepository) : ActionStorage<List<Circle>> {

   override fun getActionClass(): Class<out CachedAction<*>> = GetCirclesCommand::class.java

   override fun save(params: CacheBundle?, data: List<Circle>) = db.saveCircles(data)

   override fun get(action: CacheBundle?): List<Circle> = db.circles
}
