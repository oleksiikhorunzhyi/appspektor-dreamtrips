package com.messenger.ui.presenter;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.messenger.constant.CursorLoaderIds;
import com.messenger.delegate.ChatDelegate;
import com.messenger.delegate.ProfileCrosser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.activity.NewChatMembersActivity;
import com.messenger.ui.view.NewChatMembersScreen;
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

public abstract class BaseNewChatMembersScreenPresenter extends MessengerPresenterImpl<NewChatMembersScreen, NewChatLayoutViewState>
        implements NewChatScreenPresenter {

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

    protected Activity activity;
    private String textInChosenContactsEditText;

    final private ProfileCrosser profileCrosser;
    final private ContactsHeaderCreator contactsHeaderCreator;

    public BaseNewChatMembersScreenPresenter(Activity activity) {
        this.activity = activity;

        ((Injector) activity.getApplicationContext()).inject(this);

        textInChosenContactsEditText = activity
                .getString(R.string.new_chat_chosen_contacts_header_empty);

        profileCrosser = new ProfileCrosser(activity, routeCreator);
        contactsHeaderCreator = new ContactsHeaderCreator(activity);
    }

    @Override
    public void attachView(NewChatMembersScreen view) {
        super.attachView(view);
        dreamSpiceManager.start(activity);
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
        NewChatMembersScreen screen = getView();
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
