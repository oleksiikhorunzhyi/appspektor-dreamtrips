package com.worldventures.dreamtrips.social.service

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.service.invites.*
import io.techery.janet.ActionPipe
import rx.schedulers.Schedulers

class InviteShareInteractor(sessionPipeCreator: SessionActionPipeCreator) {

   val membersPipe: ActionPipe<MembersCommand> = sessionPipeCreator
         .createPipe(MembersCommand::class.java, Schedulers.io())
   val addContactPipe: ActionPipe<AddContactCommand> = sessionPipeCreator
         .createPipe(AddContactCommand::class.java, Schedulers.io())
   val selectContactPipe: ActionPipe<SelectContactCommand> = sessionPipeCreator
         .createPipe(SelectContactCommand::class.java, Schedulers.io())
   val deseseltAllContactsPipe: ActionPipe<DeselectAllContactsCommand> = sessionPipeCreator
         .createPipe(DeselectAllContactsCommand::class.java, Schedulers.io())
   val invitesTemplatePipe: ActionPipe<GetInviteTemplatesCommand> = sessionPipeCreator
         .createPipe(GetInviteTemplatesCommand::class.java, Schedulers.io())
   private val updateContactsWithSentInvitesPipe: ActionPipe<UpdateContactsWithSentInvitesCommand> = sessionPipeCreator
         .createPipe(UpdateContactsWithSentInvitesCommand::class.java, Schedulers.io())
   val updateContactsPipe: ActionPipe<UpdateContactsCommand> = sessionPipeCreator
         .createPipe(UpdateContactsCommand::class.java, Schedulers.io())
   val createFilledInvitePipe: ActionPipe<CreateFilledInviteCommand> = sessionPipeCreator
         .createPipe(CreateFilledInviteCommand::class.java, Schedulers.io())
   val sendInvitesPipe: ActionPipe<SendInvitesCommand> = sessionPipeCreator
         .createPipe(SendInvitesCommand::class.java, Schedulers.io())

   init {
      updateContactsPipe.observeSuccess()
            .subscribe {
               membersPipe.send(RefreshContactsCommand(it.result))
               updateContactsWithSentInvitesPipe.send(UpdateContactsWithSentInvitesCommand(it.result))
            }
      updateContactsWithSentInvitesPipe.observeSuccess()
            .subscribe {
               membersPipe.send(RefreshContactsCommand(it.result))
            }
      addContactPipe.observeSuccess()
            .subscribe {
               membersPipe.send(AddContactToStorageCommand(it.result))
            }
   }
}
