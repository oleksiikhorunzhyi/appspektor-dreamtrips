package com.worldventures.dreamtrips.modules.membership.api;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PhoneContactRequest extends SpiceRequest<ArrayList<Member>> {

    @InviteTemplate.Type
    private int type;

    @Inject Context context;
    @Inject SnappyRepository db;

    public PhoneContactRequest(@InviteTemplate.Type int type) {
        super((Class<ArrayList<Member>>) new ArrayList<Member>().getClass());
        this.type = type;
    }

    @Override
    public ArrayList<Member> loadDataFromNetwork() {
        return readContacts();
    }

    public ArrayList<Member> readContacts() {
        Uri contentURI = null;
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = new String[0];
        String order = ContactsContract.Contacts.DISPLAY_NAME + " ASC";
        switch (type) {
            case InviteTemplate.EMAIL:
                contentURI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
                projection = new String[]{
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts._ID,
                        ContactsContract.CommonDataKinds.Email.DATA};
                break;
            case InviteTemplate.SMS:
                contentURI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                projection = new String[]{
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE};
                selection = ContactsContract.CommonDataKinds.Phone.TYPE + " = ?";
                selectionArgs = new String[]{String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)};
                break;
        }
        Cursor cur = context.getContentResolver().query(contentURI, projection, selection, selectionArgs, order);

        ArrayList<Member> result = new ArrayList<>();
        while (cur.moveToNext()) {
            Member member = new Member();
            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            member.setId(id);
            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            member.setName(name);
            switch (type) {
                case InviteTemplate.EMAIL:
                    String email = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    member.setEmail(email);
                    if (TextUtils.isEmpty(member.getEmail())) break;
                    if (TextUtils.isEmpty(member.getName())) member.setName(email);
                    member.setEmailIsMain(true);
                    result.add(member);
                    break;
                case InviteTemplate.SMS:
                    String phone = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    member.setPhone(phone);
                    if (TextUtils.isEmpty(phone)) break;
                    if (TextUtils.isEmpty(member.getName())) member.setName(phone);
                    member.setEmailIsMain(false);
                    result.add(member);
                    break;
            }
        }
        cur.close();

        // Load members from db and filter out with empty phone/email
        Predicate<Member> memberPredicate = null;
        switch (type) {
            case InviteTemplate.EMAIL:
                memberPredicate = element -> !element.getEmail().isEmpty();
                break;
            case InviteTemplate.SMS:
                memberPredicate = element -> !element.getPhone().isEmpty();
                break;
        }
        List<Member> cachedMembers = db.getInviteMembers();
        cachedMembers = Queryable.from(cachedMembers).filter(memberPredicate).toList();
        result.addAll(0, cachedMembers);

        return result;
    }

}
