package com.worldventures.dreamtrips.social.ui.profile.service.command;


import com.worldventures.core.model.Circle;
import com.worldventures.core.model.User;
import com.worldventures.core.service.command.api_action.ApiActionCommand;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.circles.RemoveFriendsFromCircleHttpAction;

import java.util.Collections;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RemoveFriendFromCircleCommand extends ApiActionCommand<RemoveFriendsFromCircleHttpAction, Void> {

   private Circle circle;
   private int userId;

   public RemoveFriendFromCircleCommand(Circle circle, User friend) {
      this.circle = circle;
      this.userId = friend.getId();
   }

   public int getUserId() {
      return userId;
   }

   public Circle getCircle() {
      return circle;
   }

   @Override
   protected RemoveFriendsFromCircleHttpAction getHttpAction() {
      return new RemoveFriendsFromCircleHttpAction(circle.getId(), Collections.singletonList(userId));
   }

   @Override
   protected Class<RemoveFriendsFromCircleHttpAction> getHttpActionClass() {
      return RemoveFriendsFromCircleHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_remove_user_from_circle;
   }
}
