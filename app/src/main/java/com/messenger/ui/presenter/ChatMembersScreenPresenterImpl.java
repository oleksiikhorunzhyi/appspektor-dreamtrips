package com.messenger.ui.presenter;

import android.content.Context;
import android.database.Cursor;
import android.text.SpannableString;
import android.view.View;

import com.messenger.delegate.ChatDelegate;
import com.messenger.delegate.ProfileCrosser;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.view.add_member.ChatMembersScreen;
import com.messenger.ui.viewstate.ChatMembersScreenViewState;
import com.messenger.util.ContactsHeaderCreator;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

import static com.worldventures.dreamtrips.core.module.RouteCreatorModule.PROFILE;



public abstract class ChatMembersScreenPresenterImpl extends MessengerPresenterImpl<ChatMembersScreen, ChatMembersScreenViewState>
        implements ChatMembersScreenPresenter {

    @Inject
    DataUser user;
    @Inject
    @Named(PROFILE)
    RouteCreator<Integer> routeCreator;
    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    DreamSpiceManager dreamSpiceManager;
    @Inject
    ChatDelegate chatDelegate;

    protected Observable<Cursor> cursorObservable;

    private String textInChosenContactsEditText;

    final private ProfileCrosser profileCrosser;
    final private ContactsHeaderCreator contactsHeaderCreator;

    public ChatMembersScreenPresenterImpl(Context context) {
        super(context);

        ((Injector) (context.getApplicationContext())).inject(this);

        textInChosenContactsEditText = context
                .getString(R.string.new_chat_chosen_contacts_header_empty);

        profileCrosser = new ProfileCrosser(context, routeCreator);
        contactsHeaderCreator = new ContactsHeaderCreator(context);
    }

    @Override
    public void attachView(ChatMembersScreen view) {
        super.attachView(view);
        dreamSpiceManager.start(getContext());
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        dreamSpiceManager.shouldStop();
    }

    @Override
    public void onNewViewState() {
        state = new ChatMembersScreenViewState();
        state.setLoadingState(ChatMembersScreenViewState.LoadingState.LOADING);
        applyViewState();
    }

    @Override
    public void openUserProfile(DataUser user) {
        profileCrosser.crossToProfile(user);
    }

    @Override
    public void applyViewState() {
        ChatMembersScreenViewState viewState = getViewState();
        ChatMembersScreen screen = getView();
        List<DataUser> selectedContacts = viewState.getSelectedContacts();

        switch (viewState.getLoadingState()) {
            case LOADING:
                screen.showLoading();
                break;
            case CONTENT:
                screen.showContent();
                break;
            case ERROR:
                screen.showError(viewState.getError());
                break;
        }

        if (selectedContacts != null) {
            screen.setSelectedContacts(selectedContacts);
            refreshSelectedContactsHeader();
        }
        int conversationNameVisibility = getViewState()
                .isChatNameEditTextVisible() ? View.VISIBLE : View.GONE;
        getView().setConversationNameEditTextVisibility(conversationNameVisibility);
    }

    @Override
    public void onSelectedUsersStateChanged(List<DataUser> selectedContacts) {
        getViewState().setSelectedContacts(selectedContacts);
        refreshSelectedContactsHeader();
    }

    private void refreshSelectedContactsHeader() {
        List<DataUser> selectedContacts = getViewState().getSelectedContacts();
        SpannableString spannableString = contactsHeaderCreator.createHeader(selectedContacts);
        textInChosenContactsEditText = spannableString.toString();
        getView().setSelectedUsersHeaderText(spannableString);
    }

    @Override
    public void onTextChangedInChosenContactsEditText(String text) {
        int textInChosenContactsLength = textInChosenContactsEditText.length();
        int textLength = text.length();

        if (textInChosenContactsLength > textLength) {
            List<DataUser> selectedContacts = getViewState().getSelectedContacts();
            if (selectedContacts != null && !selectedContacts.isEmpty()) {
                selectedContacts.remove(selectedContacts.size() - 1);
                getView().setSelectedContacts(selectedContacts);
                onSelectedUsersStateChanged(selectedContacts);
            }
            return;
        }
        String searchQuery = text.substring(textInChosenContactsLength, textLength);
        getViewState().setSearchFilter(searchQuery);

        cursorObservable.subscribe(cursor ->
                getView().setContacts(cursor, searchQuery, UsersDAO.USER_DISPLAY_NAME));
    }

    protected void showContacts(Cursor cursor) {
        if (!isViewAttached()) return;

        getViewState().setLoadingState(ChatMembersScreenViewState.LoadingState.CONTENT);
        getView().setContacts(cursor);
        getView().showContent();
    }

    protected void slideInConversationNameEditText() {
        getView().slideInConversationNameEditText();
        getViewState().setChatNameEditTextVisible(true);
    }

    protected void slideOutConversationNameEditText() {
        getView().slideOutConversationNameEditText();
        getViewState().setChatNameEditTextVisible(false);
    }

    @Override
    public int getToolbarMenuRes() {
        return R.menu.new_chat;
    }
}
