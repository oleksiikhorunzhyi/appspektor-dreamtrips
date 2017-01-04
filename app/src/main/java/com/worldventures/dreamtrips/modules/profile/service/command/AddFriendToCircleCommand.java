package com.worldventures.dreamtrips.modules.profile.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.circles.AddFriendsToCircleHttpAction;
import com.worldventures.dreamtrips.core.api.action.ApiActionCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.Collections;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class AddFriendToCircleCommand extends ApiActionCommand<AddFriendsToCircleHttpAction, Void> {

   private Circle circle;
   private int userId;

   public AddFriendToCircleCommand(Circle circle, User friend) {
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
   protected AddFriendsToCircleHttpAction getHttpAction() {
      return new AddFriendsToCircleHttpAction(circle.getId(), Collections.singletonList(userId));
   }

   @Override
   protected Class<AddFriendsToCircleHttpAction> getHttpActionClass() {
      return AddFriendsToCircleHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_add_user_to_circle;
   }
}
