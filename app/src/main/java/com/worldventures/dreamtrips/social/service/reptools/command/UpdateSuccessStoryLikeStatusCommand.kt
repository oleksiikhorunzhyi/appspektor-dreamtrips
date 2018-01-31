package com.worldventures.dreamtrips.social.service.reptools.command

import com.worldventures.dreamtrips.social.service.reptools.command.operation.StoryLikeStatusStorageOperation
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class UpdateSuccessStoryLikeStatusCommand(storyId: Int, liked: Boolean) : SuccessStoriesCommand(StoryLikeStatusStorageOperation(storyId, liked))
