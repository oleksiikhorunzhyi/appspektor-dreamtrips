package com.worldventures.dreamtrips.social.service.invites

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.text.TextUtils
import com.worldventures.core.janet.dagger.InjectableAction
import com.worldventures.dreamtrips.core.utils.ProjectPhoneNumberUtils
import com.worldventures.dreamtrips.social.domain.entity.Contact
import com.worldventures.dreamtrips.social.domain.entity.InviteType
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import javax.inject.Inject

@CommandAction
class UpdateContactsCommand(private val type: InviteType) : Command<List<Contact>>(), InjectableAction {

   @field:Inject lateinit var context: Context

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<Contact>>) {
      Observable.just(readContacts()).subscribe(callback::onSuccess, callback::onFail)
   }

   private fun readContacts(): List<Contact> {
      val contentURI: Uri
      val projection: Array<String>
      when (type) {
         InviteType.EMAIL -> {
            contentURI = ContactsContract.CommonDataKinds.Email.CONTENT_URI
            projection = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Email.DATA)
         }
         InviteType.SMS -> {
            contentURI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            projection = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE)
         }
      }

      val cur = context.contentResolver.query(contentURI, projection, null, null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC")
      val result = mutableListOf<Contact>()
      while (cur.moveToNext()) {
         val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
         if (!TextUtils.isEmpty(name)) {
            val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
            val email = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
            val phone = ProjectPhoneNumberUtils.normalizeNumber(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))
            result.add(Contact(id, name, phone, email, type == InviteType.EMAIL))
         }
      }
      cur.close()

      return result.distinctBy { it.name }
   }
}

