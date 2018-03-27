package com.worldventures.dreamtrips.social.service.profile.command

import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.core.service.command.api_action.ApiActionCommand
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.circles.AddFriendsToCircleHttpAction

import io.techery.janet.command.annotations.CommandAction

@CommandAction
class AddFriendToCircleCommand(val circle: Circle, friend: User) : ApiActionCommand<AddFriendsToCircleHttpAction, Void>() {

   val userId = friend.id

   override fun getHttpAction() = AddFriendsToCircleHttpAction(circle.id, listOf(userId))

   override fun getHttpActionClass() = AddFriendsToCircleHttpAction::class.java

   override fun getFallbackErrorMessage() = R.string.error_failed_to_add_user_to_circle

}
