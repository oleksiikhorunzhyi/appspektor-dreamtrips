package com.worldventures.dreamtrips.social.domain.storage.action

import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage
import com.worldventures.dreamtrips.social.service.reptools.command.ReadSuccessStoriesCommand
import com.worldventures.dreamtrips.social.service.reptools.command.RefreshSuccessStoriesCommand
import com.worldventures.dreamtrips.social.service.reptools.command.UpdateSuccessStoryLikeStatusCommand
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory
import com.worldventures.janet.cache.storage.MemoryStorage

class SuccessStoriesStorage : MemoryStorage<List<SuccessStory>>(), MultipleActionStorage<List<SuccessStory>> {

   override fun getActionClasses() = listOf(ReadSuccessStoriesCommand::class.java,
         RefreshSuccessStoriesCommand::class.java,
         UpdateSuccessStoryLikeStatusCommand::class.java)
}
