package com.worldventures.dreamtrips.social.service.invites.operation

import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation
import com.worldventures.dreamtrips.social.domain.entity.Contact

class UpdateContactOperation(val contact: Contact) : ListStorageOperation<Contact> {

   override fun perform(items: MutableList<Contact>): MutableList<Contact> {
      val index = items.indexOfFirst { contact.id == it.id }
      if (index >= 0) items[index] = contact
      return items
   }
}