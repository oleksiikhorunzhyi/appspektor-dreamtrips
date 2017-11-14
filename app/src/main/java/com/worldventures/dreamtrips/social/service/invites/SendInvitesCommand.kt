package com.worldventures.dreamtrips.social.service.invites

import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.invitation.CreateInvitationHttpAction
import com.worldventures.dreamtrips.api.invitation.model.ImmutableCreateInvitationParams
import com.worldventures.dreamtrips.api.invitation.model.InvitationType
import com.worldventures.dreamtrips.social.domain.entity.InviteType
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class SendInvitesCommand(private val templateId: Int, private val contacts: List<String>, private val type: InviteType)
   : CommandWithError<Boolean>(), InjectableAction {

   @field:Inject lateinit var janet: Janet

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Boolean>) {
      janet.createPipe(CreateInvitationHttpAction::class.java)
            .createObservableResult(CreateInvitationHttpAction(provideParams()))
            .map { true }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   private fun provideParams() = ImmutableCreateInvitationParams.builder()
         .templateId(templateId)
         .contacts(contacts)
         .type(if (type === InviteType.SMS) InvitationType.SMS else InvitationType.EMAIL)
         .build()

   override fun getFallbackErrorMessage() = R.string.error_fail_to_get_preview_for_template
}
