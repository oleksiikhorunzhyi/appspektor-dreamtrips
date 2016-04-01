package com.messenger.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataUser;
import com.worldventures.dreamtrips.R;

import java.util.Collection;

public class ContactsHeaderCreator {

    private final Context context;

    public ContactsHeaderCreator(Context context) {
        this.context = context;
    }

    public Pair<String, SpannableString> createHeader(Collection<DataUser> contacts) {
        String selectedContactsCountString;
        if (contacts.isEmpty()) {
            selectedContactsCountString = context.getString(R.string.new_chat_chosen_contacts_header_empty);
        } else {
            String addString = context.getString(R.string.new_chat_chosen_contacts_header_contacts_list_start_value);
            selectedContactsCountString = String.format("%s: (%d)", addString, contacts.size());
        }

        String[] userNames = Queryable.from(contacts).map(DataUser::getName).toArray(String.class);
        String userNamesString = TextUtils.join(", ", userNames);
        if (!TextUtils.isEmpty(userNamesString)) userNamesString += ", "; // add coma even to latest user
        SpannableString contactsListSpannableString = new SpannableString(userNamesString);
        int previousUsernameIndex = 0;
        for (String userName : userNames) {
            int userNameIndexStart = userNamesString.indexOf(userName, previousUsernameIndex);
            int userNameIndexEnd = userNameIndexStart + userName.length();
            previousUsernameIndex = userNameIndexEnd;
            assignUnderlinedSpan(contactsListSpannableString, userNameIndexStart, userNameIndexEnd);
            int coloredSpanEnd = userNameIndexEnd + 1; // +1 for coma
            assignBlueSpan(contactsListSpannableString, userNameIndexStart, coloredSpanEnd);
        }

        return new Pair<>(selectedContactsCountString, contactsListSpannableString);
    }

    private void assignUnderlinedSpan(SpannableString spannableString, int start, int end) {
        spannableString.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void assignBlueSpan(SpannableString spannableString, int start, int end) {
        int spannableColor = ContextCompat.getColor(context, R.color.contact_list_header_selected_contacts);
        spannableString.setSpan(new ForegroundColorSpan(spannableColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
