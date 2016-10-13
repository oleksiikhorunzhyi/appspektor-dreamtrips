package com.worldventures.dreamtrips.modules.membership.service.command;



import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class GetPhoneContactsCommand extends Command<List<Member>> implements InjectableAction{

   @Inject Context context;

   private InviteTemplate.Type type;

   public GetPhoneContactsCommand(InviteTemplate.Type type) {
      this.type = type;
   }

   @Override
   protected void run(CommandCallback<List<Member>> callback) throws Throwable {
      Observable.just(readContacts())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private List<Member> readContacts() {
      Uri contentURI = null;
      String[] projection = null;
      String selection = null;
      String[] selectionArgs = new String[0];
      String order = ContactsContract.Contacts.DISPLAY_NAME + " ASC";
      switch (type) {
         case EMAIL:
            contentURI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            projection = new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID, ContactsContract.CommonDataKinds.Email.DATA};
            break;
         case SMS:
            contentURI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE};
            break;
      }
      Cursor cur = context.getContentResolver().query(contentURI, projection, selection, selectionArgs, order);

      List<Member> result = new ArrayList<>();
      while (cur.moveToNext()) {
         Member member = new Member();
         String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
         member.setId(id);

         String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
         if (TextUtils.isEmpty(name)) continue;
         else member.setName(name);

         switch (type) {
            case EMAIL:
               String email = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
               member.setEmail(email);
               if (TextUtils.isEmpty(member.getEmail())) break;
               if (TextUtils.isEmpty(member.getName())) break;
               member.setEmailIsMain(true);
               result.add(member);
               break;
            case SMS:
               String phone = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
               member.setPhone(PhoneNumberUtils.normalizeNumber(phone));
               if (TextUtils.isEmpty(phone)) break;
               if (TextUtils.isEmpty(member.getName())) break;
               member.setEmailIsMain(false);
               result.add(member);
               break;
         }
      }
      cur.close();

      return Queryable.from(result).distinct().toList();
   }

}

