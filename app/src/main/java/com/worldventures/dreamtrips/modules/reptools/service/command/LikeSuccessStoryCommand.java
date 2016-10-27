package com.worldventures.dreamtrips.modules.reptools.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.success_stories.LikeSuccessStoryHttpAction;
import com.worldventures.dreamtrips.core.api.action.ApiActionCommand;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LikeSuccessStoryCommand extends ApiActionCommand<LikeSuccessStoryHttpAction, Void> {

   int id;

   public LikeSuccessStoryCommand(int id) {
      this.id = id;
   }

   @Override
   protected Void mapHttpActionResult(LikeSuccessStoryHttpAction httpAction) {
      return null;
   }

   @Override
   protected LikeSuccessStoryHttpAction getHttpAction() {
      return new LikeSuccessStoryHttpAction(id);
   }

   @Override
   protected Class<LikeSuccessStoryHttpAction> getHttpActionClass() {
      return LikeSuccessStoryHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_like_success_story;
   }
}
