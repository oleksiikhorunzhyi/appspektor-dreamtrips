package com.messenger.ui.presenter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;

import com.messenger.delegate.ProfileCrosser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.messenger.ui.view.edit_member.EditChatMembersScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.ui.viewstate.EditChatMembersViewState;
import com.messenger.ui.viewstate.LceViewState;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.util.ActivityWatcher;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import flow.Flow;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
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

    private final ProfileCrosser profileCrosser;

    private final String conversationId;
    private Observable<DataConversation> conversationObservable;
    private Observable<MultiUserChat> chatObservable;
    private Observable<Cursor> membersCursorObservable;

    private PublishSubject<Void> adapterInitializer = PublishSubject.create();
    private Observable<Void> adapterInitializeObservable;

    public EditChatMembersScreenPresenterImpl(Context context, String conversationId) {
        super(context);
        ((Injector) context.getApplicationContext()).inject(this);

        this.conversationId = conversationId;

        this.profileCrosser = new ProfileCrosser(context, routeCreator);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        getViewState().setLoadingState(LceViewState.LoadingState.LOADING);
        getView().showLoading();

        connectConversation();
        connectChat();
        connectParticipants();

        adapterInitializeObservable = adapterInitializer.replay(1).autoConnect();

        activityWatcher.addOnStartStopListener(startStopAppListener);
    }

    @Override
    public void onDetachedFromWindow() {
        closeChat();
        super.onDetachedFromWindow();
        activityWatcher.removeOnStartStopListener(startStopAppListener);
    }

    private void closeChat() {
        chatObservable.first().subscribeOn(Schedulers.io()).subscribe(Chat::close);
    }

    private void connectConversation() {
        conversationObservable = conversationsDAO.getConversation(conversationId)
                .compose(new NonNullFilter<>())
                .compose(bindViewIoToMainComposer())
                .replay(1)
                .autoConnect();
    }

    private void connectChat() {
        chatObservable = conversationObservable
                .first()
                .map(conversation -> messengerServerFacade.getChatManager()
                        .createMultiUserChat(conversationId, conversation.getOwnerId()))
                .compose(bindViewIoToMainComposer())
                .replay(1)
                .autoConnect();
    }

    private void connectParticipants() {
        membersCursorObservable = participantsDAO.getParticipants(conversationId)
                .compose(bindViewIoToMainComposer())
                .replay(1)
                .autoConnect();

        membersCursorObservable.subscribe(cursor -> {
            getViewState().setLoadingState(LceViewState.LoadingState.CONTENT);
            showContent();
        });
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
    public void onSaveInstanceState(Bundle bundle) {
        //if presenter is recreated and view has previous state with LOADING status,
        //in applyViewState method all observables will be null cause onAttachedToWindow method calls after one
        getViewState().setLoadingState(LceViewState.LoadingState.LOADING);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onSearchFilterSelected(String search) {
        getViewState().setSearchFilter(search);
        applyViewState();
    }

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
        switch (editChatMembersViewState.getLoadingState()) {
            case LOADING:
                view.showLoading();
                break;
            case CONTENT:
                showContent();
                break;
            case ERROR:
                view.showError(editChatMembersViewState.getError());
                break;
        }
    }

    private void showContent() {
        Observable.zip(adapterInitializeObservable, membersCursorObservable, (aVoid, cursor) -> cursor)
                .compose(bindVisibilityIoToMainComposer())
                .subscribe(cursor -> {
                    // cause admin of group chat is also participant
                    if (cursor.getCount() <= 1) {
                        Flow.get(getContext()).set(ConversationsPath.MASTER_PATH);
                        return;
                    }

                    EditChatMembersScreen view = getView();
                    view.showContent();
                    view.setMembers(cursor, getViewState().getSearchFilter(), UsersDAO.USER_DISPLAY_NAME);
                    view.setTitle(String.format(getContext().getString(R.string.edit_chat_members_title), cursor.getCount()));
                });
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
        chatObservable.subscribe(chat -> {
            chat.kick(Collections.singletonList(user.getId()))
                    .map(users -> users.get(0))
                    .doOnNext(memberId -> participantsDAO.delete(conversationId, memberId))
                    .subscribe(s -> {}, e -> Timber.e(e, ""));
        });
    }

    @Override
    public void onUserClicked(DataUser user) {
        profileCrosser.crossToProfile(user);
    }

    @Override
    public void requireAdapterInfo() {
        conversationObservable
                .distinctUntilChanged()
                .subscribe(conversation -> {
                    getView().setAdapterWithInfo(user, isOwner(conversation));
                    adapterInitializer.onNext(null);
                });
    }

    ////////////////////////////////////////////////////////
    ////   Helpers
    ////////////////////////////////////////////////////////

    private boolean isOwner(DataConversation conversation) {
        return TextUtils.equals(user.getId(), conversation.getOwnerId());
    }
}
