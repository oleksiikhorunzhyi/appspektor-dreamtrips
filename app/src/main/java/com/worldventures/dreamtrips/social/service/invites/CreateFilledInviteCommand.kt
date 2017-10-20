package com.worldventures.dreamtrips.social.service.invites

import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.janet.dagger.InjectableAction
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.invitation.CreateFilledInvitationTemplateHttpAction
import com.worldventures.dreamtrips.api.invitation.model.ImmutableFilledInvitationParams
import com.worldventures.dreamtrips.social.domain.entity.InviteTemplate
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import javax.inject.Inject

@CommandAction
class CreateFilledInviteCommand(private val id: Int, private val message: String) : CommandWithError<InviteTemplate>(), InjectableAction {

   @field:Inject lateinit var janet: Janet
   @field:Inject lateinit var mappery: MapperyContext

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<InviteTemplate>) {
      janet.createPipe(CreateFilledInvitationTemplateHttpAction::class.java)
            .createObservableResult(CreateFilledInvitationTemplateHttpAction(provideParams()))
            .map { mappery.convert(it.response(), InviteTemplate::class.java) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   private fun provideParams() = ImmutableFilledInvitationParams.builder()
         .templateId(id)
         .message(message)
         .build()

   override fun getFallbackErrorMessage() = R.string.error_fail_to_get_preview_for_template
}
