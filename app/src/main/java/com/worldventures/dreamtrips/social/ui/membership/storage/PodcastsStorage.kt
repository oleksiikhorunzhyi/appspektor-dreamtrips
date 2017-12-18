package com.worldventures.dreamtrips.social.ui.membership.storage

import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.core.janet.cache.storage.CombinedListStorage
import com.worldventures.core.janet.cache.storage.Storage
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast
import com.worldventures.dreamtrips.social.ui.membership.service.command.GetPodcastsCommand

class PodcastsStorage(memoryStorage: Storage<List<Podcast>>, diskStorage: Storage<List<Podcast>>) :
      CombinedListStorage<Podcast>(memoryStorage, diskStorage), ActionStorage<List<Podcast>> {

   override fun getActionClass() = GetPodcastsCommand::class.java
}
