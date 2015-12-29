package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.activity.EditChatMembersActivity;
import com.messenger.ui.view.EditChatMembersScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.ui.viewstate.EditChatMembersViewState;
import com.messenger.ui.viewstate.LceViewState;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditChatMembersScreenPresenterImpl extends BaseViewStateMvpPresenter<EditChatMembersScreen,
        EditChatMembersViewState> implements EditChatMembersScreenPresenter {

    private Activity activity;

    private Conversation conversation;
    private Cursor membersCursor;

    private RxContentResolver contentResolver;
    private Subscription participantsSubscriber;

    public EditChatMembersScreenPresenterImpl(Activity activity) {
        this.activity = activity;

        this.activity = activity;
        String conversationId = activity.getIntent()
                .getStringExtra(EditChatMembersActivity.EXTRA_CONVERSATION_ID);

        contentResolver = new RxContentResolver(activity.getContentResolver(), query -> {
            return FlowManager.getDatabaseForTable(User.class).getWritableDatabase()
                    .rawQuery(query.selection + " " + query.sortOrder, query.selectionArgs);
        });

        conversation = new Select()
                .from(Conversation.class)
                .byIds(conversationId)
                .querySingle();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        getViewState().setLoadingState(LceViewState.LoadingState.LOADING);
        getView().showLoading();

        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users u " +
                                "JOIN ParticipantsRelationship p " +
                                "ON p.userId = u._id " +
                                "WHERE p.conversationId = ?")
                .withSortOrder("ORDER BY " + User.COLUMN_NAME + " COLLATE NOCASE ASC")
                .withSelectionArgs(new String[]{conversation.getId()}).build();
        participantsSubscriber = contentResolver.query(q, User.CONTENT_URI,
                ParticipantsRelationship.CONTENT_URI)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
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
        switch (getViewState().getLoadingState()) {
            case LOADING:
                getView().showLoading();
                break;
            case CONTENT:
                getView().showContent();
                getView().setMembers(membersCursor, getViewState().getSearchFilter(),
                        User.COLUMN_NAME);
                String title = String.format(activity.getString(R.string.edit_chat_members_title),
                        membersCursor.getCount());
                getView().setTitle(title);
                break;
            case ERROR:
                getView().showError(getViewState().getError());
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
        Toast.makeText(activity, "Not implemented", Toast.LENGTH_SHORT).show();
    }
}
