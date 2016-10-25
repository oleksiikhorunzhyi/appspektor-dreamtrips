package com.worldventures.dreamtrips.modules.reptools.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.success_stories.LikeSuccessStoryHttpAction;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LikeSuccessStoryCommand extends BaseLikeActionCommand<LikeSuccessStoryHttpAction> {

   public LikeSuccessStoryCommand(int id) {
      super(id);
   }

   @Override
   Class<LikeSuccessStoryHttpAction> getActionClass() {
      return LikeSuccessStoryHttpAction.class;
   }

   @Override
   LikeSuccessStoryHttpAction getAction() {
      return new LikeSuccessStoryHttpAction(id);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_like_success_story;
   }
}
