package com.worldventures.dreamtrips.social.service.reptools.command

import com.worldventures.core.service.command.api_action.ApiActionCommand
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.success_stories.UnlikeSuccessStoryHttpAction

import io.techery.janet.command.annotations.CommandAction

@CommandAction
class UnlikeSuccessStoryCommand(private val id: Int) : ApiActionCommand<UnlikeSuccessStoryHttpAction, Int>() {

   override fun getHttpAction(): UnlikeSuccessStoryHttpAction = UnlikeSuccessStoryHttpAction(id)

   override fun getHttpActionClass(): Class<UnlikeSuccessStoryHttpAction> = UnlikeSuccessStoryHttpAction::class.java

   override fun getFallbackErrorMessage() = R.string.error_fail_to_unlike_success_story

   override fun mapCommandResult(httpCommandResult: Int?) = id
}
