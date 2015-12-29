package com.messenger.ui.presenter;

import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.storege.utils.ParticipantsDAO;
import com.messenger.ui.activity.EditChatMembersActivity;
import com.messenger.ui.view.EditChatMembersScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.ui.viewstate.EditChatMembersViewState;
import com.messenger.ui.viewstate.LceViewState;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.techery.spares.module.Injector;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;

import java.util.Collections;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class EditChatMembersScreenPresenterImpl extends BaseViewStateMvpPresenter<EditChatMembersScreen,
        EditChatMembersViewState> implements EditChatMembersScreenPresenter {

    private final MultiUserChat chat;
    private Activity activity;

    private Conversation conversation;
    private Cursor membersCursor;

    private RxContentResolver contentResolver;

    @Inject
    MessengerServerFacade messengerServerFacade;

    @Inject
    User user;

    public EditChatMembersScreenPresenterImpl(Activity activity) {
        this.activity = activity;
        ((Injector) activity.getApplication()).inject(this);

        String conversationId = activity.getIntent()
                .getStringExtra(EditChatMembersActivity.EXTRA_CONVERSATION_ID);

        contentResolver = new RxContentResolver(activity.getContentResolver(),
                query -> FlowManager.getDatabaseForTable(User.class).getWritableDatabase()
                        .rawQuery(query.selection + " " + query.sortOrder, query.selectionArgs));

        Conversation conversation = new Select()
                .from(Conversation.class)
                .byIds(conversationId)
                .querySingle();
        this.conversation = conversation;
        chat = messengerServerFacade.getChatManager()
                .createMultiUserChat(conversation.getId(), conversation.getOwnerId());
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        getViewState().setLoadingState(LceViewState.LoadingState.LOADING);
        getView().showLoading();
        ParticipantsDAO.selectParticipants(contentResolver, conversation.getId(), User.CONTENT_URI, ParticipantsRelationship.CONTENT_URI)
                .onBackpressureLatest()                .subscribeOn(Schedulers.io())
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
                .doOnNext(user1 -> ParticipantsDAO.delete(activity.getContentResolver(), conversation.getId(), user1.getId()))
                .doOnError(e -> Timber.e(e, ""))
                .subscribe();
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
