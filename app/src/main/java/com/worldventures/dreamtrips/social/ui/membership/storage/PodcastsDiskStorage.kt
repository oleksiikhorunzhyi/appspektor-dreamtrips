package com.worldventures.dreamtrips.social.ui.membership.storage

import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedDiskStorage
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast

import rx.functions.Action1
import rx.functions.Func0

class PodcastsDiskStorage(private val db: SocialSnappyRepository) : PaginatedDiskStorage<Podcast>() {

   override fun getRestoreAction() = Func0 { db.podcasts }

   override fun getSaveAction() = Action1<List<Podcast>> { db.savePodcasts(it) }

}
