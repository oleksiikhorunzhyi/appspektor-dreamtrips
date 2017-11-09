package com.worldventures.dreamtrips.social.ui.profile.service.command;

import com.worldventures.core.model.Circle;
import com.worldventures.core.model.User;
import com.worldventures.core.service.command.api_action.ApiActionCommand;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.circles.AddFriendsToCircleHttpAction;

import java.util.Collections;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class AddFriendToCircleCommand extends ApiActionCommand<AddFriendsToCircleHttpAction, Void> {

   private final Circle circle;
   private final int userId;

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
