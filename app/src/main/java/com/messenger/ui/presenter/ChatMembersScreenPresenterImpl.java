package com.messenger.ui.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.delegate.CreateConversationHelper;
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
import com.messenger.util.StringUtils;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

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
    CreateConversationHelper createConversationHelper;
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

    public ChatMembersScreenPresenterImpl(Context context, Injector injector) {
        super(context);

       injector.inject(this);

        profileCrosser = new ProfileCrosser(context, routeCreator);
        contactsHeaderCreator = new ContactsHeaderCreator(context);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        connectToContacts();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    protected Observable<List<DataUser>> createContactListObservable() {
        return usersDAO
                .getFriends(user.getId())
                .compose(bindView())
                .subscribeOn(Schedulers.io());
    }

    protected void connectToContacts() {
        Observable<CharSequence> selectedContactsWithSearchQuery = getView().getSearchQueryObservable();

        selectedContactsWithSearchQuery.subscribe(this::processSelectedContactsHeaderChange);

        Observable<CharSequence> searchQueryObservable = selectedContactsWithSearchQuery
                .compose(bindView())
                .filter(this::searchFilterAvailable)
                .map(this::prepareSearchQuery);

        getExistingAndFutureParticipants()
                .compose(bindView())
                .flatMap(existingAndFutureParticipantsPair ->
                    searchHelper.search(createContactListObservable(), searchQueryObservable,
                            (user, filter) -> StringUtils.containsIgnoreCase(user.getDisplayedName(), filter))
                    .compose(userSectionHelper.prepareItemInCheckableList(
                             existingAndFutureParticipantsPair.first,
                             existingAndFutureParticipantsPair.second))
                    .map(pair -> pair.first))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::addListItems);
    }

    @NonNull
    private Observable<Pair<List<DataUser>, List<DataUser>>> getExistingAndFutureParticipants() {
        return Observable.zip(getExistingParticipants(), Observable.just(futureParticipants), Pair::new)
                .compose(bindView());
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

    private void processSelectedContactsHeaderChange(CharSequence contactsWithSearchQuery) {
        if (searchFilterAvailable(contactsWithSearchQuery)) {
            CharSequence query = prepareSearchQuery(contactsWithSearchQuery);
            if (!TextUtils.equals(currentSearchFilter, query)) {
                currentSearchFilter = query.toString();
                refreshSelectedContactsHeader();
            }
        } else {
            removeLastUserIfExist();
        }
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
        }, e -> Timber.e(e, "Could not get participants"));
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
        if (getView() == null) return;
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
