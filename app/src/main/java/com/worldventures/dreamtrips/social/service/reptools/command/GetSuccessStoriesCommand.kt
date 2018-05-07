package com.worldventures.dreamtrips.social.service.reptools.command

import com.worldventures.core.service.command.api_action.MappableApiActionCommand
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.success_stories.GetSuccessStoriesHttpAction
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory

import io.techery.janet.command.annotations.CommandAction

@CommandAction
class GetSuccessStoriesCommand : MappableApiActionCommand<GetSuccessStoriesHttpAction, List<SuccessStory>, SuccessStory>() {

   override fun getHttpAction(): GetSuccessStoriesHttpAction = GetSuccessStoriesHttpAction()

   override fun getHttpActionClass(): Class<GetSuccessStoriesHttpAction> = GetSuccessStoriesHttpAction::class.java

   override fun getMappingTargetClass(): Class<SuccessStory> = SuccessStory::class.java

   override fun mapHttpActionResult(httpAction: GetSuccessStoriesHttpAction) = httpAction.response()

   override fun getFallbackErrorMessage() = R.string.error_fail_to_load_success_stories
}
