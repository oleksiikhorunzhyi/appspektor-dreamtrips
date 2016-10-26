package com.worldventures.dreamtrips.modules.profile.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.circles.RemoveFriendsFromCircleHttpAction;
import com.worldventures.dreamtrips.core.api.action.ApiActionCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

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
