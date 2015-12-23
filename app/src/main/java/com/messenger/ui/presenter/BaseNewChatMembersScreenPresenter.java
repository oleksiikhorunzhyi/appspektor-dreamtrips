package com.messenger.ui.presenter;

import android.app.Activity;
import android.database.ContentObservable;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.messenger.constant.CursorLoaderIds;
import com.messenger.delegate.LoaderDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.model.ChatUser;
import com.messenger.ui.activity.NewChatMembersActivity;
import com.messenger.ui.view.NewChatMembersScreen;
import com.messenger.ui.viewstate.NewChatLayoutViewState;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public abstract class BaseNewChatMembersScreenPresenter extends BaseViewStateMvpPresenter<NewChatMembersScreen, NewChatLayoutViewState>
        implements NewChatScreenPresenter, LoaderManager.LoaderCallbacks<Cursor> {

    private LoaderDelegate loaderDelegate;

    @Inject
    SessionHolder<UserSession> appSessionHolder;
    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    DreamSpiceManager dreamSpiceManager;
    @Inject
    User user;

    private String textInChosenContactsEditText = "";

    protected Activity activity;

    private Cursor contactsCursor;

    public static NewChatScreenPresenter createPresenter(Activity activity) {
        int mode = activity.getIntent().getIntExtra(NewChatMembersActivity.EXTRA_MODE, -1);
        if (mode == NewChatMembersActivity.MODE_NEW_CHAT) {
            return new NewChatScreenPresenterImpl(activity);
        } else if (mode == NewChatMembersActivity.MODE_CHAT_ADD_MEMBERS) {
            return new AddChatMembersScreenPresenterImpl(activity);
        }
        throw new IllegalArgumentException("Cannot find presenter for mode provided");
    }

    public BaseNewChatMembersScreenPresenter(Activity activity) {
        this.activity = activity;

        ((Injector) activity.getApplicationContext()).inject(this);

        loaderDelegate = new LoaderDelegate(activity, messengerServerFacade, dreamSpiceManager);

        textInChosenContactsEditText = activity
                .getString(R.string.new_chat_chosen_contacts_header_empty);
    }

    @Override
    public void attachView(NewChatMembersScreen view) {
        super.attachView(view);
        dreamSpiceManager.start(view.getContext());
        loadChatContacts();
        getView().setConversationNameEditTextVisibility(View.GONE);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        dreamSpiceManager.shouldStop();
        ((AppCompatActivity) activity).getSupportLoaderManager().destroyLoader(CursorLoaderIds.CONTACT_LOADER);
    }

    @Override
    public void onNewViewState() {
        state = new NewChatLayoutViewState();
        getView().showLoading();
        getViewState().setLoadingState(NewChatLayoutViewState.LoadingState.LOADING);
    }

    @Override
    public void applyViewState() {
        switch (getViewState().getLoadingState()) {
            case LOADING:
                getView().showLoading();
                break;
            case CONTENT:
                getView().showContent();
                break;
            case ERROR:
                getView().showError(getViewState().getError());
                break;
        }
        if (getViewState().getSelectedContacts() != null) {
            getView().setSelectedContacts(getViewState().getSelectedContacts());
            refreshSelectedContactsHeader();
        }
    }

    public void loadChatContacts() {
        loaderDelegate.loadContacts();
    }

    protected void initContactsLoaders() {
        LoaderManager loaderManager = ((AppCompatActivity) activity).getSupportLoaderManager();
        Loader loader = loaderManager.getLoader(CursorLoaderIds.CONTACT_LOADER);
        if (loader == null) {
            loaderManager.initLoader(CursorLoaderIds.CONTACT_LOADER, null, this);
        } else {
            loaderManager.restartLoader(CursorLoaderIds.CONTACT_LOADER, null, this);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        showContacts(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        showContacts(null);
    }

    @Override
    public void onSelectedUsersStateChanged(List<User> selectedContacts) {
        getViewState().setSelectedContacts(selectedContacts);
        refreshSelectedContactsHeader();
    }

    private void refreshSelectedContactsHeader() {
        List<User> selectedContacts = getViewState().getSelectedContacts();
        StringBuilder sb = new StringBuilder();
        sb.append(activity.getString(R.string.new_chat_chosen_contacts_header_contacts_list_start_value));
        if (!selectedContacts.isEmpty()) {
            sb.append(" (");
            sb.append(String.valueOf(selectedContacts.size()));
            sb.append(")");
        }
        sb.append(": ");

        List<String> userNames = new ArrayList<>();

        for (int i = 0; i < selectedContacts.size(); i++) {
            ChatUser user = selectedContacts.get(i);
            CharSequence name = user.getName();
            sb.append(name);
            userNames.add(name.toString());
            sb.append(", ");
        }

        String resultString = sb.toString();
        SpannableString spannableString = new SpannableString(resultString);

        for (int i = 0; i < userNames.size(); i++) {
            String name = userNames.get(i);
            int spanBeginning = resultString.indexOf(name);
            int underlinedSpanEnding = spanBeginning + name.length();
            int coloredSpanEnding = underlinedSpanEnding;
            coloredSpanEnding++;
            assignUnderlinedSpan(spannableString, spanBeginning, underlinedSpanEnding);
            assignBlueSpan(spannableString, spanBeginning, coloredSpanEnding);
        }

        textInChosenContactsEditText = resultString;

        getView().setSelectedUsersHeaderText(spannableString);
    }

    private void assignUnderlinedSpan(SpannableString spannableString, int start, int end) {
        spannableString.setSpan(new UnderlineSpan(), start,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void assignBlueSpan(SpannableString spannableString, int start, int end) {
        int spannableColor = activity.getResources()
                .getColor(R.color.contact_list_header_selected_contacts);
        spannableString.setSpan(new ForegroundColorSpan(spannableColor), start,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void onTextChangedInChosenContactsEditText(String text) {
        if (textInChosenContactsEditText.length() > text.length()) {
            List<User> selectedContacts = getViewState().getSelectedContacts();
            if (selectedContacts != null && !selectedContacts.isEmpty()) {
                getViewState().getSelectedContacts().
                        remove(getViewState().getSelectedContacts().size() - 1);
                getView().setSelectedContacts(getViewState().getSelectedContacts());
                onSelectedUsersStateChanged(getViewState().getSelectedContacts());
            }
            return;
        }
        String searchQuery = text.substring(textInChosenContactsEditText.length(),
                text.length());
        getViewState().setSearchFilter(searchQuery);
        getView().setContacts(contactsCursor, searchQuery, User.COLUMN_NAME);
    }

    protected void showContacts(Cursor cursor) {
        if (!isViewAttached()) {
            return;
        }
        getViewState().setLoadingState(NewChatLayoutViewState.LoadingState.CONTENT);
        getView().setContacts(cursor);
        getView().showContent();
        contactsCursor = cursor;
    }

    protected void saveChatModifications(Conversation conversation, List<User> participants, @Nullable String subject) {
        MultiUserChat multiUserChat = messengerServerFacade.getChatManager()
                .createMultiUserChat(conversation.getId(), user, true);
        multiUserChat.invite(participants);
        if (!TextUtils.isEmpty(subject) && !(TextUtils.getTrimmedLength(subject) > 0)) {
            multiUserChat.setSubject(subject);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.new_chat, menu);
        return true;
    }
}
