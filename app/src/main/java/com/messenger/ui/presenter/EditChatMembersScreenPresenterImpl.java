package com.messenger.ui.presenter;

import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;

import com.messenger.delegate.ProfileCrosser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.storege.utils.ConversationsDAO;
import com.messenger.storege.utils.ParticipantsDAO;
import com.messenger.ui.activity.EditChatMembersActivity;
import com.messenger.ui.view.EditChatMembersScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.ui.viewstate.EditChatMembersViewState;
import com.messenger.ui.viewstate.LceViewState;
import com.techery.spares.module.Injector;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.worldventures.dreamtrips.core.module.RouteCreatorModule.PROFILE;

public class EditChatMembersScreenPresenterImpl extends BaseViewStateMvpPresenter<EditChatMembersScreen,
        EditChatMembersViewState> implements EditChatMembersScreenPresenter {

    @Inject
    @Named(PROFILE)
    RouteCreator<Integer> routeCreator;
    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    User user;

    private Activity activity;
    private final MultiUserChat chat;
    private final ProfileCrosser profileCrosser;

    private final String conversationId;
    private Conversation conversation;

    private Cursor membersCursor;

    private ParticipantsDAO participantsDAO;
    private ConversationsDAO conversationsDAO;

    public Subscription subscriptionConversations;
    public Subscription subscriptionParticipants;

    public EditChatMembersScreenPresenterImpl(Activity activity) {
        this.activity = activity;
        ((Injector) activity.getApplication()).inject(this);
        participantsDAO = new ParticipantsDAO(activity.getApplication());
        conversationsDAO = new ConversationsDAO(activity.getApplication());

        conversationId = activity.getIntent()
                .getStringExtra(EditChatMembersActivity.EXTRA_CONVERSATION_ID);
        chat = messengerServerFacade.getChatManager()
                .createMultiUserChat(conversationId, conversation.getOwnerId());

        this.profileCrosser = new ProfileCrosser(activity, routeCreator);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        getViewState().setLoadingState(LceViewState.LoadingState.LOADING);
        getView().showLoading();

        subscriptionConversations = conversationsDAO.getConversation(conversationId)
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindView((View) getView()))
                .subscribe(conversation -> {
                    this.conversation = conversation;
                });

        subscriptionParticipants = participantsDAO.getParticipants(conversationId)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindView((View) getView()))
                .subscribe(cursor -> {
                    getViewState().setLoadingState(LceViewState.LoadingState.CONTENT);
                    membersCursor = cursor;
                    applyViewState();
                });
    }

    @Override
    public void onDetachedFromWindow() {
        getViewState().setLoadingState(LceViewState.LoadingState.LOADING);
        super.onDetachedFromWindow();
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
        EditChatMembersScreen view = getView();
        assert view != null;
        switch (getViewState().getLoadingState()) {
            case LOADING:
                view.showLoading();
                break;
            case CONTENT:
                view.showContent();
                view.setMembers(membersCursor, getViewState().getSearchFilter(), User.COLUMN_NAME);
                view.setTitle(String.format(activity.getString(R.string.edit_chat_members_title), membersCursor.getCount()));
                break;
            case ERROR:
                view.showError(getViewState().getError());
                break;
        }
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
        chat.kick(Collections.singletonList(user))
                .map(users -> users.get(0))
                .doOnNext(user1 -> participantsDAO.delete(conversationId, user1.getId()))
                .doOnError(e -> Timber.e(e, ""))
                .subscribe();
    }

    @Override
    public void onUserClicked(User user) {
        profileCrosser.crossToProfile(user);
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public boolean isOwner() {
        return user.getId().equals(conversation.getOwnerId());
    }
}
