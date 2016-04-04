package com.messenger.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
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

    public ContactsHeaderInfo createHeader(Collection<DataUser> users, String searchQuery) {
        String selectedContactsFormattedCount = getSelectedContactsFormattedCount(users);

        String userNamesComaSeparated = Queryable.from(users).map(DataUser::getName)
                .fold("", (list, username) -> list + username + ", ");
        Spanned userNamesSpanned = getSpannedUserNames(users, userNamesComaSeparated);
        Spanned searchQuerySpanned = getSpannedSearchQuery(searchQuery);
        Spanned userNamesWithSearchQuerySpanned = (Spanned) TextUtils.concat(userNamesSpanned, searchQuerySpanned);

        return new ContactsHeaderInfo(selectedContactsFormattedCount,
                userNamesComaSeparated, userNamesWithSearchQuerySpanned);
    }

    /**
     * Get first part of the selected contacts edit text message
     * @param contacts
     * @return formatted count of selected contacts, e.g. Add(3):
     */
    private String getSelectedContactsFormattedCount(Collection<DataUser> contacts) {
        String selectedContactsFormattedCount;
        if (contacts.isEmpty()) {
            selectedContactsFormattedCount = context.getString(R.string.new_chat_chosen_contacts_header_empty);
        } else {
            String addString = context.getString(R.string.new_chat_chosen_contacts_header_contacts_list_start_value);
            selectedContactsFormattedCount = String.format("%s (%d):", addString, contacts.size());
        }
        return selectedContactsFormattedCount;
    }

    private Spanned getSpannedUserNames(Collection<DataUser> users, String usernamesComaSeparated) {
        Spanned userNamesComaSeparatedSpanned =  new SpannableString(usernamesComaSeparated);
        // save previous found username index each time to handle
        // the case with the same usernames going right after each other in the list
        int previousProcessedUserNameIndex = 0;
        for (DataUser user : users) {
            String userName = user.getName();
            int userNameIndexStart = usernamesComaSeparated.indexOf(userName, previousProcessedUserNameIndex);
            int userNameIndexEnd = userNameIndexStart + userName.length();

            assignUnderlinedSpan((Spannable) userNamesComaSeparatedSpanned, userNameIndexStart, userNameIndexEnd);
            // assign blue span to the coma after username also
            assignBlueSpan((Spannable) userNamesComaSeparatedSpanned, userNameIndexStart, userNameIndexEnd + 1);

            previousProcessedUserNameIndex = userNameIndexEnd;
        }
        return userNamesComaSeparatedSpanned;
    }

    private Spanned getSpannedSearchQuery(CharSequence searchQuery) {
        if (TextUtils.isEmpty(searchQuery)) {
            return new SpannableString("");
        }
        SpannableString searchQuerySpanned = new SpannableString(searchQuery);
        assignBlueSpan(searchQuerySpanned, 0, searchQuery.length());
        return searchQuerySpanned;
    }

    private void assignUnderlinedSpan(Spannable spannableString, int start, int end) {
        spannableString.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void assignBlueSpan(Spannable spannableString, int start, int end) {
        int spannableColor = ContextCompat.getColor(context, R.color.contact_list_header_selected_contacts);
        spannableString.setSpan(new ForegroundColorSpan(spannableColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static class ContactsHeaderInfo {
        String selectedContactsFormattedCount;
        String contactsList;
        Spanned contactsListWithSearchQuery;

        public ContactsHeaderInfo(String selectedContactsFormattedCount, String contactsList,
                                  Spanned contactsListWithSearchQuery) {
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

        public Spanned getContactsListWithSearchQuery() {
            return contactsListWithSearchQuery;
        }
    }
}
