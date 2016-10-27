package com.worldventures.dreamtrips.modules.reptools.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.success_stories.LikeSuccessStoryHttpAction;
import com.worldventures.dreamtrips.api.success_stories.UnlikeSuccessStoryHttpAction;
import com.worldventures.dreamtrips.api.success_stories.model.ImmutableSuccessStory;
import com.worldventures.dreamtrips.core.api.action.ApiActionCommand;

import io.techery.janet.command.annotations.CommandAction;


@CommandAction
public class UnlikeSuccessStoryCommand extends ApiActionCommand<UnlikeSuccessStoryHttpAction, Void> {

   int id;

   public UnlikeSuccessStoryCommand(int id) {
      this.id = id;
   }

   @Override
   protected Void mapHttpActionResult(UnlikeSuccessStoryHttpAction httpAction) {
      return null;
   }

   @Override
   protected UnlikeSuccessStoryHttpAction getHttpAction() {
      return new UnlikeSuccessStoryHttpAction(id);
   }

   @Override
   protected Class<UnlikeSuccessStoryHttpAction> getHttpActionClass() {
      return UnlikeSuccessStoryHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_unlike_success_story;
   }
}
