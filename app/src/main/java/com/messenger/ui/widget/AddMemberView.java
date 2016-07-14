package com.messenger.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.messenger.entities.DataUser;
import com.worldventures.dreamtrips.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.functions.Action1;

public class AddMemberView extends RelativeLayout {

    private final List<Action1<DataUser>> removeUserCallbacks = new ArrayList<>();

    @InjectView(R.id.new_chat_chosen_contacts_count_editText) TextView chosenContactsCountEditText;
    @InjectView(R.id.new_chat_chosen_contacts_edittext) SelectionListenerEditText chosenContactsListEditText;

    @Nullable CharSequence lastChosenContacts;
    @Nullable CharSequence currentSearchFilter;

    @Nullable List<DataUser> chosenUsers;

    private Observable<CharSequence> searchObservable;

    public AddMemberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
    }

    public AddMemberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AddMemberView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context);
    }

    private void inflate(Context context) {
        View.inflate(context, R.layout.widget_add_members, this);
        ButterKnife.inject(this, this);

        searchObservable = RxTextView.afterTextChangeEvents(chosenContactsListEditText)
                .map(TextViewAfterTextChangeEvent::editable)
                .flatMap(this::processChangingText)
                .publish()
                .autoConnect();

        chosenContactsListEditText.setSelectionListener((s, a)
                -> chosenContactsListEditText.setSelection(chosenContactsListEditText.getText().length()));
    }

    public void setChosenUsers(@NotNull List<DataUser> chosenUsers) {
        this.chosenUsers = chosenUsers;
        updateChosenUsers();
    }

    private void setSelectedUsersHeaderText(CharSequence selectedContactsCount, CharSequence searchLine) {
        chosenContactsCountEditText.setText(selectedContactsCount);
        chosenContactsListEditText.setText(searchLine);
        chosenContactsListEditText.setSelection(searchLine.length());
    }

    private Observable<CharSequence> processChangingText(CharSequence contactsWithSearchQuery) {
        if (!TextUtils.isEmpty(lastChosenContacts) && contactsWithSearchQuery.length() < lastChosenContacts.length()) {
            removeLastUserIfExist();
            return Observable.empty();
        }
        currentSearchFilter = getSearchQuery(contactsWithSearchQuery);
        return Observable.just(currentSearchFilter);
    }

    private void updateChosenUsers() {
        lastChosenContacts = buildSpannedUserNames(chosenUsers);
        CharSequence searchLine = TextUtils.isEmpty(currentSearchFilter) ?
                lastChosenContacts : TextUtils.concat(lastChosenContacts, currentSearchFilter);
        setSelectedUsersHeaderText(buildSelectedContactsFormattedCount(chosenUsers), searchLine);
    }

    public void setQuery(@Nullable CharSequence query) {
        currentSearchFilter = query;
    }

    private boolean removeLastUserIfExist() {
        if (chosenUsers == null || chosenUsers.isEmpty()) return false;
        DataUser removedUser = chosenUsers.remove(chosenUsers.size() - 1);
        for (Action1<DataUser> action : removeUserCallbacks) action.call(removedUser);
        updateChosenUsers();
        return true;
    }

    private CharSequence getSearchQuery(@NotNull CharSequence contactsWithSearchQuery) {
        String contactsWithQuery = contactsWithSearchQuery.toString();
        return TextUtils.isEmpty(lastChosenContacts)
                ? contactsWithQuery
                : contactsWithQuery.substring(lastChosenContacts.length());
    }

    public Observable<CharSequence> getQueryObservable() {
        return searchObservable;
    }

    public void addRemovedUserCallback(Action1<DataUser> action) {
        removeUserCallbacks.add(action);
    }

    public void removeRemovedUserCallback(Action1<DataUser> action) {
        removeUserCallbacks.remove(action);
    }

    public Observable<DataUser> getRemovedUserObservable() {
        Action1<DataUser> [] callbackRef = (Action1<DataUser>[]) new Action1[] {null};
        return Observable.<DataUser>create(subscriber -> {
            callbackRef[0] = subscriber::onNext;
            addRemovedUserCallback(callbackRef[0]);
        }).doOnUnsubscribe(() -> removeRemovedUserCallback(callbackRef[0]));
    }

    private String buildSelectedContactsFormattedCount(Collection<DataUser> contacts) {
        if (contacts.isEmpty()) {
            return getContext().getString(R.string.new_chat_chosen_contacts_header_empty);
        }
        String addString = getContext().getString(R.string.new_chat_chosen_contacts_header_contacts_list_start_value);
        return String.format("%s (%d):", addString, contacts.size());
    }

    @NonNull
    private Spanned buildSpannedUserNames(Collection<DataUser> users) {
        // TODO: 7/18/16 refactor this shit
        String userNamesComaSeparated = Queryable.from(users).map(DataUser::getName)
                .fold("", (list, username) -> list + username + ", ");
        Spanned userNamesComaSeparatedSpanned = new SpannableString(userNamesComaSeparated);
        // save previous found username index each time to handle
        // the case with the same usernames going right after each other in the list
        int previousProcessedUserNameIndex = 0;

        for (DataUser user : users) {
            String userName = user.getName();
            int userNameIndexStart = userNamesComaSeparated.indexOf(userName, previousProcessedUserNameIndex);
            int userNameIndexEnd = userNameIndexStart + userName.length();

            assignUnderlinedSpan((Spannable) userNamesComaSeparatedSpanned, userNameIndexStart, userNameIndexEnd);

            previousProcessedUserNameIndex = userNameIndexEnd;
        }
        return userNamesComaSeparatedSpanned;
    }

    private void assignUnderlinedSpan(Spannable spannableString, int start, int end) {
        spannableString.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
