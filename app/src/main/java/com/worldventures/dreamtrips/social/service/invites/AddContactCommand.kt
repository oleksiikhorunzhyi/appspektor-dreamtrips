package com.worldventures.dreamtrips.social.service.invites

import android.accounts.AccountManager
import android.content.ContentProviderOperation
import android.content.Context
import android.provider.ContactsContract
import com.worldventures.core.janet.dagger.InjectableAction
import com.worldventures.dreamtrips.social.domain.entity.Contact
import com.worldventures.dreamtrips.social.domain.entity.InviteType
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import java.util.*
import javax.inject.Inject

@CommandAction
class AddContactCommand(private val name: String, private val email: String, private val phone: String, private val currentSelectedType: InviteType) : Command<Contact>(), InjectableAction {

   @field:Inject lateinit var context: Context

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Contact>) {
      val operations = ArrayList<ContentProviderOperation>()
      operations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, AccountManager.KEY_ACCOUNT_TYPE)
            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, AccountManager.KEY_ACCOUNT_NAME)
            .build())
      operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
            .build())
      operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
            .build())
      operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_MOBILE)
            .withYieldAllowed(true)
            .build())

      context.contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
      callback.onSuccess(Contact("", name, phone, email, currentSelectedType == InviteType.EMAIL))
   }
}
