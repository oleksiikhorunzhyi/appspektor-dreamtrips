package com.worldventures.dreamtrips.social.service.invites

import com.worldventures.dreamtrips.social.domain.entity.Contact
import com.worldventures.dreamtrips.social.service.invites.operation.UpdateContactOperation
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class SelectContactCommand(val contact: Contact) : MembersCommand(UpdateContactOperation(contact))