package com.messenger.ui.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;

import com.messenger.delegate.ChatDelegate;
import com.messenger.delegate.ProfileCrosser;
import com.messenger.delegate.RxSearchHelper;
import com.messenger.ui.util.UserSectionHelper;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.ui.model.SelectableDataUser;
import com.messenger.ui.view.add_member.ChatMembersScreen;
import com.messenger.ui.viewstate.ChatMembersScreenViewState;
import com.messenger.util.ContactsHeaderCreator;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;

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

    private final RxSearchHelper<DataUser> searchHelper = new RxSearchHelper<>();
    protected final Set<DataUser> selectedUsers = new ConcurrentSkipListSet<>();
    private final UserSectionHelper userSectionHelper;
    @Nullable
    private CharSequence textInChosenContactsEditText;

    private final ProfileCrosser profileCrosser;
    private final ContactsHeaderCreator contactsHeaderCreator;

    public ChatMembersScreenPresenterImpl(Context context) {
        super(context);

        ((Injector) context.getApplicationContext()).inject(this);

        userSectionHelper = new UserSectionHelper(context, user);
        profileCrosser = new ProfileCrosser(context, routeCreator);
        contactsHeaderCreator = new ContactsHeaderCreator(context);
    }

    @Override
    public void attachView(ChatMembersScreen view) {
        super.attachView(view);
        dreamSpiceManager.start(getContext());
        connectToContacts();
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        dreamSpiceManager.shouldStop();
    }

    protected abstract Observable<List<DataUser>> createContactListObservable();

    protected void connectToContacts() {
        ConnectableObservable<CharSequence> chosenObservable = getView().getChosenObservable()
                .compose(bindView())
                .publish();

        chosenObservable
                .filter(text -> !availableFilter(text))
                .subscribe(sequence -> resetSelectedContactsHeader());

        Observable<CharSequence> filterObservable = chosenObservable
                .compose(bindView())
                .filter(this::availableFilter)
                .map(this::prepareSearchQuery);

        searchHelper.search(createContactListObservable(), filterObservable,
                (user, searchFilter) -> searchHelper.contains(user.getDisplayedName(), searchFilter))
                .compose(userSectionHelper.prepareItemInCheckableList(selectedUsers))
                .compose(bindView())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemWithUserCount -> addListItems(itemWithUserCount.first));

        chosenObservable.connect();
    }

    private String prepareSearchQuery(CharSequence text) {
        String str = text.toString();
        return TextUtils.isEmpty(textInChosenContactsEditText) ? "" : str.substring(textInChosenContactsEditText.length());
    }

    private boolean availableFilter(CharSequence text) {
        return TextUtils.isEmpty(textInChosenContactsEditText) || textInChosenContactsEditText.length() <= text.length();
    }

    @Override
    public void onNewViewState() {
        state = new ChatMembersScreenViewState();
        state.setLoadingState(ChatMembersScreenViewState.LoadingState.LOADING);
        applyViewState();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        state.setSelectedContacts(selectedUsers);
        state.setLoadingState(ChatMembersScreenViewState.LoadingState.CONTENT);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void openUserProfile(DataUser user) {
        profileCrosser.crossToProfile(user);
    }

    @Override
    public void applyViewState() {
        ChatMembersScreenViewState viewState = getViewState();
        ChatMembersScreen screen = getView();

        List<DataUser> selectedUsersFromViewState = viewState.getSelectedContacts();
        selectedUsers.addAll(selectedUsersFromViewState);
        selectedUsersFromViewState.clear();
        refreshSelectedContactsHeader();

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

        int conversationNameVisibility = getViewState()
                .isChatNameEditTextVisible() ? View.VISIBLE : View.GONE;
        getView().setConversationNameEditTextVisibility(conversationNameVisibility);
    }

    @Override
    public void onItemSelectChange(SelectableDataUser item) {
        if (item.isSelected()) {
            selectedUsers.add(item.getDataUser());
        } else {
            selectedUsers.remove(item.getDataUser());
        }
        refreshSelectedContactsHeader();
    }

    private void refreshSelectedContactsHeader() {
        SpannableString spannableString = contactsHeaderCreator.createHeader(selectedUsers);
        textInChosenContactsEditText = spannableString;
        getView().setSelectedUsersHeaderText(spannableString);
    }

    private void resetSelectedContactsHeader() {
        getView().setSelectedUsersHeaderText(textInChosenContactsEditText);
    }

    protected void addListItems(List<Object> items) {
        getViewState().setLoadingState(ChatMembersScreenViewState.LoadingState.CONTENT);
        getView().setAdapterItems(items);
        getView().showContent();
    }

    protected void showConversationNameEditText(boolean show) {
        ChatMembersScreen view = getView();
        if (show) view.slideInConversationNameEditText();
        else view.slideOutConversationNameEditText();
        getViewState().setChatNameEditTextVisible(true);
    }

    @Override
    public int getToolbarMenuRes() {
        return R.menu.new_chat;
    }
}
