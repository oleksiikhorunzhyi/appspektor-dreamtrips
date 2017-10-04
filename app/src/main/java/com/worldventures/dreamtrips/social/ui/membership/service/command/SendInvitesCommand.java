package com.worldventures.dreamtrips.social.ui.membership.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.invitation.CreateInvitationHttpAction;
import com.worldventures.dreamtrips.api.invitation.model.CreateInvitationParams;
import com.worldventures.dreamtrips.api.invitation.model.ImmutableCreateInvitationParams;
import com.worldventures.dreamtrips.api.invitation.model.InvitationType;
import com.worldventures.dreamtrips.social.ui.membership.model.InviteTemplate;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SendInvitesCommand extends CommandWithError implements InjectableAction {

   @Inject Janet janet;

   private int templateId;
   private List<String> contacts;
   private InviteTemplate.Type type;

   public SendInvitesCommand(int templateId, List<String> contacts, InviteTemplate.Type type) {
      this.templateId = templateId;
      this.contacts = contacts;
      this.type = type;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_get_preview_for_template;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(CreateInvitationHttpAction.class)
            .createObservableResult(new CreateInvitationHttpAction(provideParams()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private CreateInvitationParams provideParams() {
      return ImmutableCreateInvitationParams.builder()
            .templateId(templateId)
            .contacts(contacts)
            .type(type == InviteTemplate.Type.SMS ? InvitationType.SMS : InvitationType.EMAIL)
            .build();
   }
}
