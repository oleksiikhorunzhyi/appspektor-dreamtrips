package com.worldventures.dreamtrips.modules.membership.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.invitation.GetFilledInvitationTemplateHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetFilledInviteTemplateCommand extends CommandWithError<InviteTemplate> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
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
