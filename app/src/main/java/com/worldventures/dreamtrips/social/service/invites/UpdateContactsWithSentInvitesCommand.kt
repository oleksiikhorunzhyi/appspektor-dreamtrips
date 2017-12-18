package com.worldventures.dreamtrips.social.service.invites

import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.invitation.GetInvitationsHistoryHttpAction
import com.worldventures.dreamtrips.social.domain.entity.Contact
import com.worldventures.dreamtrips.social.domain.entity.SentInvite
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import javax.inject.Inject

@CommandAction
class UpdateContactsWithSentInvitesCommand(private val contacts: List<Contact>) : CommandWithError<List<Contact>>(), InjectableAction {

   @field:Inject lateinit var janet: Janet
   @field:Inject lateinit var mappery: MapperyContext

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<Contact>>) {
      janet.createPipe(GetInvitationsHistoryHttpAction::class.java)
            .createObservableResult(GetInvitationsHistoryHttpAction())
            .map { mappery.convert(it.response(), SentInvite::class.java) }
            .map { sentInvites ->
               contacts.map {
                  it.copy(sentInvite = sentInvites.firstOrNull { invite ->
                     invite.contact.equals(if (it.emailIsMain) it.email else it.phone, ignoreCase = true)
                  })
               }
            }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_fail_to_get_invitations
}
