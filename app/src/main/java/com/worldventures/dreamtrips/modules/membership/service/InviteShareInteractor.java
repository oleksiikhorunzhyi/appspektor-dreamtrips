package com.worldventures.dreamtrips.modules.membership.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.membership.service.command.CreateFilledInviteTemplateCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.GetFilledInviteTemplateCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.GetInviteTemplatesCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.GetPhoneContactsCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.GetSentInvitesCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.SendInvitesCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class InviteShareInteractor {

   private final ActionPipe<GetInviteTemplatesCommand> getInviteTemplatesPipe;
   private final ActionPipe<GetSentInvitesCommand> getSentInvitesPipe;
   private final ActionPipe<GetPhoneContactsCommand> getPhoneContactsPipe;
   private final ActionPipe<GetFilledInviteTemplateCommand> getFilledInviteTemplatePipe;
   private final ActionPipe<CreateFilledInviteTemplateCommand> createFilledInviteTemplatePipe;
   private final ActionPipe<SendInvitesCommand> sendInvitesPipe;

   @Inject
   public InviteShareInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      getSentInvitesPipe = sessionActionPipeCreator.createPipe(GetSentInvitesCommand.class, Schedulers.io());
      getPhoneContactsPipe = sessionActionPipeCreator.createPipe(GetPhoneContactsCommand.class, Schedulers.io());
      getInviteTemplatesPipe = sessionActionPipeCreator.createPipe(GetInviteTemplatesCommand.class, Schedulers.io());
      createFilledInviteTemplatePipe = sessionActionPipeCreator.createPipe(CreateFilledInviteTemplateCommand.class,
            Schedulers.io());
      getFilledInviteTemplatePipe = sessionActionPipeCreator.createPipe(GetFilledInviteTemplateCommand.class,
            Schedulers.io());
      sendInvitesPipe = sessionActionPipeCreator.createPipe(SendInvitesCommand.class, Schedulers.io());
   }

   public ActionPipe<GetInviteTemplatesCommand> getInviteTemplatesPipe() {
      return getInviteTemplatesPipe;
   }

   public ActionPipe<GetSentInvitesCommand> getSentInvitesPipe() {
      return getSentInvitesPipe;
   }

   public ActionPipe<GetPhoneContactsCommand> getPhoneContactsPipe() {
      return getPhoneContactsPipe;
   }

   public ActionPipe<GetFilledInviteTemplateCommand> getFilledInviteTemplatePipe() {
      return getFilledInviteTemplatePipe;
   }

   public ActionPipe<CreateFilledInviteTemplateCommand> createFilledInviteTemplatePipe() {
      return createFilledInviteTemplatePipe;
   }

   public ActionPipe<SendInvitesCommand> sendInvitesPipe() {
      return sendInvitesPipe;
   }
}
