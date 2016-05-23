package com.messenger.ui.presenter;

import android.content.Context;
import android.util.Pair;

import com.messenger.delegate.ProfileCrosser;
import com.messenger.delegate.RxSearchHelper;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.GroupChat;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.ui.util.UserSectionHelper;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.messenger.ui.view.edit_member.EditChatMembersScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.ui.viewstate.EditChatMembersViewState;
import com.messenger.ui.viewstate.LceViewState;
import com.messenger.util.StringUtils;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.util.ActivityWatcher;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import flow.Flow;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.core.module.RouteCreatorModule.PROFILE;

public class EditChatMembersScreenPresenterImpl extends MessengerPresenterImpl<EditChatMembersScreen,
        EditChatMembersViewState> implements EditChatMembersScreenPresenter {

    @Inject
    @Named(PROFILE)
    RouteCreator<Integer> routeCreator;
    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    DataUser user;

    @Inject
    ParticipantsDAO participantsDAO;
    @Inject
    ConversationsDAO conversationsDAO;

    @Inject
    ActivityWatcher activityWatcher;
    @Inject
    UserSectionHelper userSectionHelper;

    private final RxSearchHelper<Pair<DataUser, String>> rxSearchHelper = new RxSearchHelper<>();
    private final ProfileCrosser profileCrosser;

    private final String conversationId;
    private Observable<DataConversation> conversationObservable;

    public EditChatMembersScreenPresenterImpl(Context context, Injector injector, String conversationId) {
        super(context);
        injector.inject(this);

        this.conversationId = conversationId;

        this.profileCrosser = new ProfileCrosser(context, routeCreator);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        getViewState().setLoadingState(LceViewState.LoadingState.LOADING);
        getView().showLoading();

        connectConversation();
        connectParticipants();

        activityWatcher.addOnStartStopListener(startStopAppListener);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        activityWatcher.removeOnStartStopListener(startStopAppListener);
    }


    private void connectConversation() {
        conversationObservable = conversationsDAO.getConversation(conversationId)
                .compose(new NonNullFilter<>())
                .replay(1)
                .autoConnect();
    }

    private void connectParticipants() {
        ConnectableObservable<List<Pair<DataUser, String>>> membersObservable =
                participantsDAO.getParticipants(conversationId).publish();

        ConnectableObservable<CharSequence> searchObservable =
                getView().getSearchObservable().publish();

        searchObservable
                .compose(bindView())
                .subscribe(filterQuery ->
                getViewState().setSearchFilter(filterQuery.toString()));

        membersObservable
                .compose(bindView())
                .filter(pairs -> pairs.size() <= 1)
                .subscribe(pairs -> Flow.get(getContext()).set(ConversationsPath.MASTER_PATH),
                        throwable -> Timber.d(throwable, ""));

        rxSearchHelper.search(membersObservable, searchObservable,
                (pair, searchFilter) -> StringUtils.containsIgnoreCase(pair.first.getDisplayedName(), searchFilter))
                .compose(userSectionHelper.groupTransformer(conversationObservable))
                .compose(bindView())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemsWithUserCount ->
                        adapterDataPrepared(itemsWithUserCount.first, itemsWithUserCount.second),
                        throwable -> Timber.d(throwable, ""));

        membersObservable.connect();
        searchObservable.connect();
    }

    private void adapterDataPrepared(List<Object> items, int userCount) {
        EditChatMembersScreen view = getView();
        view.showContent();
        view.setAdapterData(items);
        view.setTitle(String.format(getContext().getString(R.string.edit_chat_members_title), userCount));
    }


    ActivityWatcher.OnStartStopAppListener startStopAppListener = new ActivityWatcher.OnStartStopAppListener() {
        @Override
        public void onStartApplication() {
        }

        @Override
        public void onStopApplication() {
            if (getView() != null) getView().invalidateAllSwipedLayouts();
        }
    };

    @Override
    public void onNewViewState() {
        state = new EditChatMembersViewState();
        state.setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
        getView().showContent();
    }

    @Override
    public void applyViewState() {
        EditChatMembersViewState editChatMembersViewState = getViewState();
        EditChatMembersScreen view = getView();

        assert view != null;
        view.restoreSearchQuery(editChatMembersViewState.getSearchFilter());
        switch (editChatMembersViewState.getLoadingState()) {
            case LOADING:
                view.showLoading();
                break;
            case CONTENT:
                view.showContent();
                break;
            case ERROR:
                view.showError(editChatMembersViewState.getError());
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Menu
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int getToolbarMenuRes() {
        return R.menu.menu_edit_chat_members;
    }

    @Override
    public void onDeleteUserFromChat(DataUser user) {
        getView().showDeletionConfirmationDialog(user);
    }

    @Override
    public void onDeleteUserFromChatConfirmed(DataUser user) {
        messengerServerFacade.getChatManager()
                .createGroupChatObservable(conversationId, this.user.getId())
                .compose(bindViewIoToMainComposer())
                .subscribe(chat -> kickUser(chat, user.getId()),
                        e -> Timber.e(e, "Failed to create chat"));
    }

    private void kickUser(GroupChat chat, String userId) {
        chat.kick(Collections.singletonList(userId))
                .map(users -> users.get(0))
                .doOnNext(memberId -> participantsDAO.delete(conversationId, memberId))
                .subscribe(memberId -> {},
                        e -> Timber.e(e, "Failed to delete user"));
    }

    @Override
    public void onUserClicked(DataUser user) {
        profileCrosser.crossToProfile(user);
    }
}
