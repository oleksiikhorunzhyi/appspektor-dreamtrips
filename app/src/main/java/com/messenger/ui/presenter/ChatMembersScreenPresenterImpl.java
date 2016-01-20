package com.messenger.ui.presenter;

import android.content.Context;
import android.database.Cursor;
import android.text.SpannableString;
import android.view.View;

import com.messenger.delegate.ChatDelegate;
import com.messenger.delegate.ProfileCrosser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.view.ChatMembersScreen;
import com.messenger.ui.viewstate.NewChatLayoutViewState;
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



public abstract class ChatMembersScreenPresenterImpl extends MessengerPresenterImpl<ChatMembersScreen, NewChatLayoutViewState>
        implements ChatMembersScreenPresenter {

    @Inject
    User user;
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
        getView().setConversationNameEditTextVisibility(View.GONE);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        dreamSpiceManager.shouldStop();
    }

    @Override
    public void onNewViewState() {
        state = new NewChatLayoutViewState();
        state.setLoadingState(NewChatLayoutViewState.LoadingState.LOADING);

        getView().showLoading();
    }

    @Override
    public void openUserProfile(User user) {
        profileCrosser.crossToProfile(user);
    }

    @Override
    public void applyViewState() {
        NewChatLayoutViewState viewState = getViewState();
        ChatMembersScreen screen = getView();
        List<User> selectedContacts = viewState.getSelectedContacts();

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
    }

    @Override
    public void onSelectedUsersStateChanged(List<User> selectedContacts) {
        getViewState().setSelectedContacts(selectedContacts);
        refreshSelectedContactsHeader();
    }

    private void refreshSelectedContactsHeader() {
        List<User> selectedContacts = getViewState().getSelectedContacts();
        SpannableString spannableString = contactsHeaderCreator.createHeader(selectedContacts);
        textInChosenContactsEditText = spannableString.toString();
        getView().setSelectedUsersHeaderText(spannableString);
    }

    @Override
    public void onTextChangedInChosenContactsEditText(String text) {
        int textInChosenContactsLength = textInChosenContactsEditText.length();
        int textLength = text.length();

        if (textInChosenContactsLength > textLength) {
            List<User> selectedContacts = getViewState().getSelectedContacts();
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
                getView().setContacts(cursor, searchQuery, User.COLUMN_NAME));
    }

    protected void showContacts(Cursor cursor) {
        if (!isViewAttached()) return;

        getViewState().setLoadingState(NewChatLayoutViewState.LoadingState.CONTENT);
        getView().setContacts(cursor);
        getView().showContent();
    }

    @Override
    public int getToolbarMenuRes() {
        return R.menu.new_chat;
    }
}
