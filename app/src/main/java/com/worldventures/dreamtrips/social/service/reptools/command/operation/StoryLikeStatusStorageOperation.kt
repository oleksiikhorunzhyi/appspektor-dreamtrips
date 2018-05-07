package com.worldventures.dreamtrips.social.service.reptools.command.operation

import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory

class StoryLikeStatusStorageOperation(val id: Int, val liked: Boolean) : ListStorageOperation<SuccessStory> {
   override fun perform(items: MutableList<SuccessStory>?) = items?.apply {
      val index = indexOfFirst { it.id == id }
      if (index != -1) {
         items[index] = first { it.id == id }.copy(isLiked = liked)
      }
   }
}
