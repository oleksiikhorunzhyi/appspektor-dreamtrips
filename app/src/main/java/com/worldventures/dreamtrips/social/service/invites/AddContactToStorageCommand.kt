package com.worldventures.dreamtrips.social.service.invites

import com.worldventures.dreamtrips.modules.common.list_storage.operation.AddToBeginningStorageOperation
import com.worldventures.dreamtrips.social.domain.entity.Contact
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class AddContactToStorageCommand(item: Contact) : MembersCommand(AddToBeginningStorageOperation(item))