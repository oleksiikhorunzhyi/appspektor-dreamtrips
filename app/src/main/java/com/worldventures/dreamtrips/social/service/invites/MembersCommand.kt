package com.worldventures.dreamtrips.social.service.invites

import com.worldventures.dreamtrips.modules.common.list_storage.command.ListStorageCommand
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation
import com.worldventures.dreamtrips.social.domain.entity.Contact
import io.techery.janet.command.annotations.CommandAction

@CommandAction
abstract class MembersCommand(operation: ListStorageOperation<Contact>) : ListStorageCommand<Contact>(operation)

fun List<Contact>.selectedContacts() = this.filter { it.selected }

fun List<Contact>.selectedMemberAddresses() = this.selectedContacts().map { if (it.emailIsMain) it.email else it.phone }
