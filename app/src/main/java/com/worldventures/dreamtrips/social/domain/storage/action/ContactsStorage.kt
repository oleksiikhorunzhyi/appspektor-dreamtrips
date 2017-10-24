package com.worldventures.dreamtrips.social.domain.storage.action

import com.worldventures.core.janet.cache.storage.MemoryStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage
import com.worldventures.dreamtrips.social.domain.entity.Contact
import com.worldventures.dreamtrips.social.service.invites.*

class ContactsStorage : MemoryStorage<List<Contact>>(), MultipleActionStorage<List<Contact>> {

   override fun getActionClasses() = listOf(RefreshContactsCommand::class.java, ReadMembersCommand::class.java,
         SelectContactCommand::class.java, DeselectAllContactsCommand::class.java, AddContactToStorageCommand::class.java)
}
