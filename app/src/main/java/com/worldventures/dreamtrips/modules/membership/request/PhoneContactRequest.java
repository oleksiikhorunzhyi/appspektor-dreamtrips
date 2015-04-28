package com.worldventures.dreamtrips.modules.membership.request;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PhoneContactRequest extends SpiceRequest<ArrayList<Member>> {

    public static final int EMAIL = 0;
    public static final int SMS = 1;
    private int type;

    @Inject
    SnappyRepository db;

    @Inject
    Context context;
    public PhoneContactRequest(@Type int type) {
        super((Class<ArrayList<Member>>) new ArrayList<Member>().getClass());
        this.type = type;
    }

    @Override
    public ArrayList<Member> loadDataFromNetwork() {
        return readContacts();
    }


    public ArrayList<Member> readContacts() {
        ArrayList<Member> result = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                Member member = new Member();
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                member.setName(name);
                extractSms(cr, cur, member, id);
                extractEmail(cr, member, id);
                if (type == EMAIL) {
                    if (!TextUtils.isEmpty(member.getEmail())) {
                        if (TextUtils.isEmpty(member.getName())) {
                            member.setName(member.getEmail());
                        }
                        member.setEmailIsMain(true);
                        result.add(member);
                    }
                } else if (type == SMS) {
                    if (!TextUtils.isEmpty(member.getPhone())) {
                        if (TextUtils.isEmpty(member.getName())) {
                            member.setName(member.getPhone());
                        }
                        member.setEmailIsMain(false);
                        result.add(member);
                    }
                }
            }
        }
        cur.close();

        List<Member> inviteMembers = db.getInviteMembers();
        List<Member> members = null;
        if (type == EMAIL) {
            members = Queryable.from(inviteMembers).filter(element -> !element.getEmail().isEmpty()).toList();
        } else if (type == SMS) {
            members = Queryable.from(inviteMembers).filter(element -> !element.getPhone().isEmpty()).toList();
        }
        result.addAll(0, members);

        return result;
    }

    private void extractEmail(ContentResolver cr, Member member, String id) {
        if (type == EMAIL) {
            // get email and type
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    new String[]{id}, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                member.setEmail(email);
            }
            emailCur.close();
        }
    }

    private void extractSms(ContentResolver cr, Cursor cur, Member member, String id) {
        if (type == SMS) {
            if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                // get the phone number
                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                while (pCur.moveToNext()) {
                    String phone = pCur.getString(
                            pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    member.setPhone(phone);
                }
                pCur.close();
            }
        }
    }

    @IntDef({EMAIL, SMS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }
}
