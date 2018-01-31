package com.worldventures.dreamtrips.social.service.reptools.command

import com.worldventures.dreamtrips.modules.common.list_storage.command.ListStorageCommand
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory

abstract class SuccessStoriesCommand(operation: ListStorageOperation<SuccessStory>, private val predicate: (SuccessStory) -> Boolean = { true })
   : ListStorageCommand<SuccessStory>(operation) {

   fun filteredResult() = result?.filter(predicate)?.sortedStories()
}

fun List<SuccessStory>.sortedStories() = this.sortedWith(compareBy({ it.category }, { it.author }))
