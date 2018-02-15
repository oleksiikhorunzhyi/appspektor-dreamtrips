package com.worldventures.dreamtrips.social.service.profile.command

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.core.service.command.api_action.ApiActionCommand
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.circles.RemoveFriendsFromCircleHttpAction

import io.techery.janet.command.annotations.CommandAction

@CommandAction
class RemoveFriendFromCircleCommand(val circle: Circle, friend: User) : ApiActionCommand<RemoveFriendsFromCircleHttpAction, Void>() {

   val userId = friend.id

   override fun getHttpAction() = RemoveFriendsFromCircleHttpAction(circle.id, listOf(userId))

   override fun getHttpActionClass() = RemoveFriendsFromCircleHttpAction::class.java

   override fun getFallbackErrorMessage() = R.string.error_failed_to_remove_user_from_circle
}
