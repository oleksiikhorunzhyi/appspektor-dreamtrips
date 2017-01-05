package com.worldventures.dreamtrips.modules.membership.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.invitation.GetInvitationsHistoryHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.membership.model.SentInvite;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetSentInvitesCommand extends CommandWithError<List<SentInvite>> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
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
