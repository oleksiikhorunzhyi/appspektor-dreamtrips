package com.worldventures.dreamtrips.social.service.invites

import com.worldventures.dreamtrips.modules.common.list_storage.operation.RefreshStorageOperation
import com.worldventures.dreamtrips.social.domain.entity.Contact
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class RefreshContactsCommand(items: List<Contact>) : MembersCommand(RefreshStorageOperation(items))
