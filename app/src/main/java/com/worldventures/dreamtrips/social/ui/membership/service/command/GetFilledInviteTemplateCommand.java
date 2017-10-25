package com.worldventures.dreamtrips.social.ui.membership.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.invitation.GetFilledInvitationTemplateHttpAction;
import com.worldventures.dreamtrips.social.ui.membership.model.InviteTemplate;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetFilledInviteTemplateCommand extends CommandWithError<InviteTemplate> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;

   private int id;

   public GetFilledInviteTemplateCommand(int id) {
      this.id = id;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_get_preview_for_template;
   }

   @Override
   protected void run(CommandCallback<InviteTemplate> callback) throws Throwable {
      janet.createPipe(GetFilledInvitationTemplateHttpAction.class)
            .createObservableResult(new GetFilledInvitationTemplateHttpAction(id))
            .map(GetFilledInvitationTemplateHttpAction::response)
            .map(invitationPreview -> mapperyContext.convert(invitationPreview, InviteTemplate.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
