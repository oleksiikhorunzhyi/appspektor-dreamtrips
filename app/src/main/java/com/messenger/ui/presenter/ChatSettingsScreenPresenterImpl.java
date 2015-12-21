package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.view.ChatSettingsScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.ui.viewstate.ChatSettingsViewState;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.techery.spares.module.Injector;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChatSettingsScreenPresenterImpl extends BaseViewStateMvpPresenter<ChatSettingsScreen,
        ChatSettingsViewState> implements ChatSettingsScreenPresenter {

    private Activity activity;

    private Conversation conversation;

    private RxContentResolver contentResolver;
    private Subscription participantsSubscriber;

    public ChatSettingsScreenPresenterImpl(Activity activity, Intent startIntent) {
        this.activity = activity;
        String conversationId = startIntent.getStringExtra(ChatActivity.EXTRA_CHAT_CONVERSATION_ID);

        contentResolver = new RxContentResolver(activity.getContentResolver(), query -> {
            return FlowManager.getDatabaseForTable(User.class).getWritableDatabase()
                    .rawQuery(query.selection, query.selectionArgs);
        });

        conversation = new Select()
                .from(Conversation.class)
                .byIds(conversationId)
                .querySingle();
    }

    @Override
    public void onNewViewState() {
        state = new ChatSettingsViewState();
        state.setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
        getView().setConversation(conversation);
        getView().showContent();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users u " +
                                "JOIN ParticipantsRelationship p " +
                                "ON p.userId = u._id " +
                                "WHERE p.conversationId = ?"
                ).withSelectionArgs(new String[]{conversation.getId()}).build();
        participantsSubscriber = contentResolver.query(q, User.CONTENT_URI,
                ParticipantsRelationship.CONTENT_URI)
                .throttleLast(100, TimeUnit.MILLISECONDS)
                .map(c -> SqlUtils.convertToList(User.class, c))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindView((View) getView()))
                .subscribe(users -> getView().setParticipants(conversation, users));

        // TODO Implement this
        // getView().setNotificationSettingStatus();
    }

    public void applyViewState() {
        if (!isViewAttached()) {
            return;
        }
        switch (getViewState().getLoadingState()) {
            case LOADING:
                getView().showLoading();
                break;
            case CONTENT:
                getView().showContent();
                break;
            case ERROR:
                getView().showError(getViewState().getError());
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Settings UI actions
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onClearChatHistoryClicked() {

    }

    @Override
    public void onLeaveChatClicked() {

    }

    @Override
    public void onNotificationsSwitchClicked(boolean isChecked) {

    }

    @Override
    public void onMembersRowClicked() {

    }

    public void onEditChatName() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.menu_chat_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_overflow:
                // overflow menu click, do nothing, wait for actual actions clicks
                return true;
            case R.id.action_edit_chat_name:
                onEditChatName();
                return true;
        }
        return false;
    }
}
