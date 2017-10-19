package com.worldventures.dreamtrips.social.ui.membership.service.command;


import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.invitation.GetInvitationTemplatesHttpAction;
import com.worldventures.dreamtrips.social.ui.membership.model.InviteTemplate;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetInviteTemplatesCommand extends CommandWithError<List<InviteTemplate>> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_invitation_templates;
   }

   @Override
   protected void run(CommandCallback<List<InviteTemplate>> callback) throws Throwable {
      janet.createPipe(GetInvitationTemplatesHttpAction.class)
            .createObservableResult(new GetInvitationTemplatesHttpAction())
            .map(GetInvitationTemplatesHttpAction::response)
            .map(invitationTemplates -> mapperyContext.convert(invitationTemplates, InviteTemplate.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
