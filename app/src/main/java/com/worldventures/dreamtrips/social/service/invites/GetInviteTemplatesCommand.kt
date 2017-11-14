package com.worldventures.dreamtrips.social.service.invites


import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.invitation.GetInvitationTemplatesHttpAction
import com.worldventures.dreamtrips.social.domain.entity.InviteTemplate
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import javax.inject.Inject

@CommandAction
class GetInviteTemplatesCommand : CommandWithError<List<InviteTemplate>>(), InjectableAction {

   @field:Inject lateinit var janet: Janet
   @field:Inject lateinit var mappery: MapperyContext

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<InviteTemplate>>) {
      janet.createPipe(GetInvitationTemplatesHttpAction::class.java)
            .createObservableResult(GetInvitationTemplatesHttpAction())
            .map { mappery.convert(it.response(), InviteTemplate::class.java) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_fail_to_invitation_templates
}

fun List<InviteTemplate>.sortedInviteTemplates() = this.sortedBy { it.category }
