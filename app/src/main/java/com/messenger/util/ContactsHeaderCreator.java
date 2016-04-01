package com.messenger.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataUser;
import com.worldventures.dreamtrips.R;

import java.util.Collection;

public class ContactsHeaderCreator {

    private final Context context;

    public ContactsHeaderCreator(Context context) {
        this.context = context;
    }

    public ContactsHeaderInfo createHeader(Collection<DataUser> contacts, String searchQuery) {
        String selectedContactsFormattedCount;
        if (contacts.isEmpty()) {
            selectedContactsFormattedCount = context.getString(R.string.new_chat_chosen_contacts_header_empty);
        } else {
            String addString = context.getString(R.string.new_chat_chosen_contacts_header_contacts_list_start_value);
            selectedContactsFormattedCount = String.format("%s: (%d)", addString, contacts.size());
        }

        String[] userNames = Queryable.from(contacts).map(DataUser::getName).toArray(String.class);
        String contactsList = TextUtils.join(", ", userNames);
        if (userNames.length > 0) contactsList += ", "; // add coma even to latest user
        String contactsListWithSearchQuery = contactsList;
        if (!TextUtils.isEmpty(searchQuery)) contactsListWithSearchQuery += searchQuery;
        SpannableString contactsListSpannableString = new SpannableString(contactsListWithSearchQuery);
        int previousUsernameIndex = 0;
        for (String userName : userNames) {
            int userNameIndexStart = contactsListWithSearchQuery.indexOf(userName, previousUsernameIndex);
            int userNameIndexEnd = userNameIndexStart + userName.length();
            previousUsernameIndex = userNameIndexEnd;
            assignUnderlinedSpan(contactsListSpannableString, userNameIndexStart, userNameIndexEnd);
            int coloredSpanEnd = userNameIndexEnd + 1; // +1 for coma
            assignBlueSpan(contactsListSpannableString, userNameIndexStart, coloredSpanEnd);
        }

        if (!TextUtils.isEmpty(searchQuery)) {
            int searchQuerySpanStartIndex = contactsListWithSearchQuery.indexOf(searchQuery, previousUsernameIndex);
            int searchQuerySpanEnd = searchQuerySpanStartIndex + searchQuery.length();
            assignBlueSpan(contactsListSpannableString, searchQuerySpanStartIndex, searchQuerySpanEnd);
        }

        return new ContactsHeaderInfo(selectedContactsFormattedCount, contactsList, contactsListSpannableString);
    }

    private void assignUnderlinedSpan(SpannableString spannableString, int start, int end) {
        spannableString.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void assignBlueSpan(SpannableString spannableString, int start, int end) {
        int spannableColor = ContextCompat.getColor(context, R.color.contact_list_header_selected_contacts);
        spannableString.setSpan(new ForegroundColorSpan(spannableColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static class ContactsHeaderInfo {
        String selectedContactsFormattedCount;
        String contactsList;
        Spannable contactsListWithSearchQuery;

        public ContactsHeaderInfo(String selectedContactsFormattedCount, String contactsList,
                                  Spannable contactsListWithSearchQuery) {
            this.selectedContactsFormattedCount = selectedContactsFormattedCount;
            this.contactsList = contactsList;
            this.contactsListWithSearchQuery = contactsListWithSearchQuery;
        }

        public String getSelectedContactsFormattedCount() {
            return selectedContactsFormattedCount;
        }

        public String getContactsList() {
            return contactsList;
        }

        public Spannable getContactsListWithSearchQuery() {
            return contactsListWithSearchQuery;
        }
    }
}
