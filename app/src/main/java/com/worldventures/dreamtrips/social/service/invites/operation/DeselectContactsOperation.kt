package com.worldventures.dreamtrips.social.service.invites.operation

import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation
import com.worldventures.dreamtrips.social.domain.entity.Contact

class DeselectContactsOperation : ListStorageOperation<Contact> {

   override fun perform(items: List<Contact>): List<Contact> {
      items.forEach { it.selected = false }
      return items
   }
}
