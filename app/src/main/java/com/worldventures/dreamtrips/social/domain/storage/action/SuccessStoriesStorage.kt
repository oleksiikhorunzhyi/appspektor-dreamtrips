package com.worldventures.dreamtrips.social.domain.storage.action

import com.worldventures.core.janet.cache.storage.MemoryStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage
import com.worldventures.dreamtrips.social.service.invites.ReadMembersCommand
import com.worldventures.dreamtrips.social.service.reptools.command.ReadSuccessStoriesCommand
import com.worldventures.dreamtrips.social.service.reptools.command.RefreshSuccessStoriesCommand
import com.worldventures.dreamtrips.social.service.reptools.command.UpdateSuccessStoryLikeStatusCommand
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory

class SuccessStoriesStorage : MemoryStorage<List<SuccessStory>>(), MultipleActionStorage<List<SuccessStory>> {

   override fun getActionClasses() = listOf(ReadSuccessStoriesCommand::class.java,
         ReadMembersCommand::class.java,
         RefreshSuccessStoriesCommand::class.java,
         UpdateSuccessStoryLikeStatusCommand::class.java)
}
