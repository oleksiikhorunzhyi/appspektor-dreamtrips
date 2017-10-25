package com.worldventures.dreamtrips.social.ui.feed.service.command;

import com.worldventures.core.service.command.api_action.ApiActionCommand;
import com.worldventures.dreamtrips.api.post.CreateVideoHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.ImmutableVideoCreationParams;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CreateVideoCommand extends ApiActionCommand<CreateVideoHttpAction, String> {

   private String uploadId;

   public CreateVideoCommand(String uploadId) {
      this.uploadId = uploadId;
   }

   @Override
   protected CreateVideoHttpAction getHttpAction() {
      return new CreateVideoHttpAction(ImmutableVideoCreationParams.builder()
            .uploadId(uploadId)
            .build());
   }

   @Override
   protected Class<CreateVideoHttpAction> getHttpActionClass() {
      return CreateVideoHttpAction.class;
   }

   @Override
   protected String mapHttpActionResult(CreateVideoHttpAction httpAction) {
      return httpAction.uid();
   }

   @Override
   public int getFallbackErrorMessage() {
      return 0;
   }
}
