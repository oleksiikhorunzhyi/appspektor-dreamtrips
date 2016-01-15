package com.messenger.ui.presenter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;

import com.messenger.delegate.ProfileCrosser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.ui.activity.EditChatMembersActivity;
import com.messenger.ui.activity.MessengerStartActivity;
import com.messenger.ui.view.EditChatMembersScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.ui.viewstate.EditChatMembersViewState;
import com.messenger.ui.viewstate.LceViewState;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
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
    User user;

    @Inject
    ParticipantsDAO participantsDAO;
    @Inject
    ConversationsDAO conversationsDAO;

    private Activity activity;
    private final ProfileCrosser profileCrosser;

    private final String conversationId;
    private Observable<Conversation> conversationObservable;
    private Observable<MultiUserChat> chatObservable;
    private Observable<Cursor> membersCursorObservable;

    private PublishSubject<Void> adapterInitializer = PublishSubject.create();
    private Observable<Void> adapterInitializeObservable;

    public EditChatMembersScreenPresenterImpl(Activity activity) {
        this.activity = activity;
        ((Injector) activity.getApplication()).inject(this);

        conversationId = activity.getIntent()
                .getStringExtra(EditChatMembersActivity.EXTRA_CONVERSATION_ID);

        this.profileCrosser = new ProfileCrosser(activity, routeCreator);
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
    }

    private void connectConversation() {
        conversationObservable = conversationsDAO.getConversation(conversationId)
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
                        MessengerStartActivity.start(activity);
                        return;
                    }

                    EditChatMembersScreen view = getView();
                    view.showContent();
                    view.setMembers(cursor, getViewState().getSearchFilter(), User.COLUMN_NAME);
                    view.setTitle(String.format(activity.getString(R.string.edit_chat_members_title), cursor.getCount()));
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        activity.getMenuInflater().inflate(R.menu.menu_edit_chat_members, menu);
        return true;
    }

    @Override
    public void onDeleteUserFromChat(User user) {
        getView().showDeletionConfirmationDialog(user);
    }

    @Override
    public void onDeleteUserFromChatConfirmed(User user) {
        chatObservable.subscribe(chat -> {
            chat.kick(Collections.singletonList(user))
                    .map(users -> users.get(0))
                    .doOnNext(member -> participantsDAO.delete(conversationId, member.getId()))
                    .doOnError(e -> Timber.e(e, ""))
                    .subscribe();
        });
    }

    @Override
    public void onUserClicked(User user) {
        profileCrosser.crossToProfile(user);
    }

    @Override
    public void requireAdapterInfo() {
        conversationObservable.subscribe(conversation -> {
            getView().setAdapterWithInfo(user, isOwner(conversation));
            adapterInitializer.onNext(null);
        });
    }

    ////////////////////////////////////////////////////////
    ////   Helpers
    ////////////////////////////////////////////////////////

    private boolean isOwner(Conversation conversation) {
        return TextUtils.equals(user.getId(), conversation.getOwnerId());
    }
}
