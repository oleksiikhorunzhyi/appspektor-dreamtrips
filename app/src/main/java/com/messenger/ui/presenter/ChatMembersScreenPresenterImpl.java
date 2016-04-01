package com.messenger.ui.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.delegate.ChatDelegate;
import com.messenger.delegate.ProfileCrosser;
import com.messenger.delegate.RxSearchHelper;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.model.SelectableDataUser;
import com.messenger.ui.util.UserSectionHelper;
import com.messenger.ui.view.add_member.ChatMembersScreen;
import com.messenger.ui.viewstate.ChatMembersScreenViewState;
import com.messenger.util.ContactsHeaderCreator;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

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
    @Inject
    UserSectionHelper userSectionHelper;
    @Inject
    UsersDAO usersDAO;

    private final RxSearchHelper<DataUser> searchHelper = new RxSearchHelper<>();
    protected final List<DataUser> futureParticipants = new CopyOnWriteArrayList<>();
    @Nullable
    private CharSequence lastChosenContacts;
    @Nullable
    private String currentSearchFilter;

    private final ProfileCrosser profileCrosser;
    private final ContactsHeaderCreator contactsHeaderCreator;

    public ChatMembersScreenPresenterImpl(Context context) {
        super(context);

        ((Injector) context.getApplicationContext()).inject(this);

        profileCrosser = new ProfileCrosser(context, routeCreator);
        contactsHeaderCreator = new ContactsHeaderCreator(context);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        dreamSpiceManager.start(getContext());
        connectToContacts();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dreamSpiceManager.shouldStop();
    }

    protected Observable<List<DataUser>> createContactListObservable() {
        return usersDAO
                .getFriends(user.getId())
                .subscribeOn(Schedulers.io());
    }

    protected void connectToContacts() {
        ConnectableObservable<CharSequence> selectedContactsWithSearchObservable = getView().getSearchQueryObservable()
                .compose(bindView())
                .publish();

        selectedContactsWithSearchObservable
                .compose(bindView())
                .subscribe(contactsWithSearchQuery -> {
                    if (searchFilterAvailable(contactsWithSearchQuery)) {
                        CharSequence query = prepareSearchQuery(contactsWithSearchQuery);
                        if (!TextUtils.equals(currentSearchFilter, query)) {
                            currentSearchFilter = query.toString();
                            refreshSelectedContactsHeader();
                        }
                    } else {
                        removeLastUserIfExist();
                    }
                });

        Observable<CharSequence> filterObservable = selectedContactsWithSearchObservable
                .compose(bindView())
                .filter(this::searchFilterAvailable)
                .map(this::prepareSearchQuery);

        getExistingAndFutureParticipants()
            .compose(bindView())
            .flatMap(participantsAndSelectedUsersPair ->
            searchHelper.search(createContactListObservable(), filterObservable,
                    (user, searchFilter) -> searchHelper.contains(user.getDisplayedName(), searchFilter))
                    .compose(userSectionHelper.prepareItemInCheckableList(
                            participantsAndSelectedUsersPair.first, participantsAndSelectedUsersPair.second))
                    .compose(bindView())
                    .observeOn(AndroidSchedulers.mainThread()))
        .subscribe(itemWithUserCount -> addListItems(itemWithUserCount.first));

        selectedContactsWithSearchObservable.connect();
    }

    @NonNull
    private Observable<Pair<List<DataUser>, List<DataUser>>> getExistingAndFutureParticipants() {
        return Observable.zip(getExistingParticipants(), Observable.just(futureParticipants), Pair::new);
    }

    private CharSequence prepareSearchQuery(CharSequence textInChosenContactsEditTextWithSearchQuery) {
        String contactsWithQuery = textInChosenContactsEditTextWithSearchQuery.toString();
        return TextUtils.isEmpty(lastChosenContacts) ? contactsWithQuery : contactsWithQuery.substring(lastChosenContacts.length());
    }

    private boolean searchFilterAvailable(CharSequence text) {
        return TextUtils.isEmpty(lastChosenContacts) || lastChosenContacts.length() <= text.length();
    }

    @Override
    public void onNewViewState() {
        state = new ChatMembersScreenViewState();
        state.setLoadingState(ChatMembersScreenViewState.LoadingState.LOADING);
        applyViewState();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        state.setSelectedContacts(futureParticipants);
        state.setSearchFilter(currentSearchFilter);
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
        futureParticipants.addAll(selectedUsersFromViewState);
        selectedUsersFromViewState.clear();
        currentSearchFilter = getViewState().getSearchFilter();
        refreshSelectedContactsHeader();
        getChatNameShouldBeVisibleObservable().subscribe(visible ->
                getView().setConversationNameEditTextVisibility(visible ? View.VISIBLE : View.GONE)
        );

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
    }

    @Override
    public void onItemSelectChange(SelectableDataUser item) {
        if (item.isSelected()) {
            futureParticipants.add(item.getDataUser());
        } else {
            futureParticipants.remove(item.getDataUser());
        }
        currentSearchFilter = null;
        refreshSelectedContactsHeader();
        getChatNameShouldBeVisibleObservable().subscribe(this::setConversationNameInputFieldVisible);
    }

    private void refreshSelectedContactsHeader() {
        getExistingAndFutureParticipants().compose(bindView()).subscribe(pair -> {
            List<DataUser> existingParticipants = pair.first;
            List<DataUser> futureParticipants = pair.second;
            List<DataUser> existingAndFutureParticipants = Queryable.from(existingParticipants)
                    .filter(participant -> !TextUtils.equals(user.getId(), participant.getId()))
                    .concat(futureParticipants).toList();
            ContactsHeaderCreator.ContactsHeaderInfo headerInfo = contactsHeaderCreator
                    .createHeader(existingAndFutureParticipants, currentSearchFilter);
            lastChosenContacts = headerInfo.getContactsList();
            getView().setSelectedUsersHeaderText(headerInfo.getSelectedContactsFormattedCount(), headerInfo.getContactsListWithSearchQuery());
        });
    }

    private boolean removeLastUserIfExist() {
        if (!futureParticipants.isEmpty()) {
            futureParticipants.remove(futureParticipants.size() - 1);
            currentSearchFilter = null;
            refreshSelectedContactsHeader();
            return true;
        }
        return false;
    }

    protected void addListItems(List<Object> items) {
        getViewState().setLoadingState(ChatMembersScreenViewState.LoadingState.CONTENT);
        getView().setAdapterItems(items);
        getView().showContent();
    }

    @SuppressWarnings("all")
    protected void setConversationNameInputFieldVisible(boolean show) {
        ChatMembersScreen view = getView();
        if (show) view.slideInConversationNameEditText();
        else view.slideOutConversationNameEditText();
    }

    @Override
    public int getToolbarMenuRes() {
        return R.menu.new_chat;
    }

    protected abstract Observable<List<DataUser>> getExistingParticipants();

    protected abstract Observable<Boolean> getChatNameShouldBeVisibleObservable();

}
