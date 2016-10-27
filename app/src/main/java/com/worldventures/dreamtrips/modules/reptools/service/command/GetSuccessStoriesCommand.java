package com.worldventures.dreamtrips.modules.reptools.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.success_stories.GetSuccessStoriesHttpAction;
import com.worldventures.dreamtrips.core.api.action.MappableBaseApiActionCommand;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetSuccessStoriesCommand extends MappableBaseApiActionCommand<GetSuccessStoriesHttpAction, List<SuccessStory>, SuccessStory> {

   @Override
   protected GetSuccessStoriesHttpAction getHttpAction() {
      return new GetSuccessStoriesHttpAction();
   }

   @Override
   protected Class<GetSuccessStoriesHttpAction> getHttpActionClass() {
      return GetSuccessStoriesHttpAction.class;
   }

   @Override
   protected Class<SuccessStory> getMappingTargetClass() {
      return SuccessStory.class;
   }

   @Override
   protected Object mapHttpActionResult(GetSuccessStoriesHttpAction httpAction) {
      return httpAction.response();
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_success_stories;
   }
}
