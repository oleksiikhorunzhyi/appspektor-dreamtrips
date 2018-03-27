package com.worldventures.dreamtrips.social.service.reptools.command

import com.worldventures.core.service.command.api_action.ApiActionCommand
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.success_stories.LikeSuccessStoryHttpAction

import io.techery.janet.command.annotations.CommandAction

@CommandAction
class LikeSuccessStoryCommand(private val id: Int) : ApiActionCommand<LikeSuccessStoryHttpAction, Int>() {

   override fun getHttpAction(): LikeSuccessStoryHttpAction = LikeSuccessStoryHttpAction(id)

   override fun getHttpActionClass(): Class<LikeSuccessStoryHttpAction> = LikeSuccessStoryHttpAction::class.java

   override fun getFallbackErrorMessage() = R.string.error_fail_to_like_success_story

   override fun mapCommandResult(httpCommandResult: Int?) = id
}
