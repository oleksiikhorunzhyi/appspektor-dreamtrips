package com.worldventures.dreamtrips.social.ui.membership.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.invitation.GetInvitationsHistoryHttpAction;
import com.worldventures.dreamtrips.social.ui.membership.model.SentInvite;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetSentInvitesCommand extends CommandWithError<List<SentInvite>> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_get_invitations;
   }

   @Override
   protected void run(CommandCallback<List<SentInvite>> callback) throws Throwable {
      janet.createPipe(GetInvitationsHistoryHttpAction.class)
            .createObservableResult(new GetInvitationsHistoryHttpAction())
            .map(GetInvitationsHistoryHttpAction::response)
            .map(invitations -> mapperyContext.convert(invitations, SentInvite.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
