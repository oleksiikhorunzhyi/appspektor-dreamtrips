package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.view.ChatSettingsScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.ui.viewstate.ChatSettingsViewState;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;

import java.util.List;

import javax.inject.Inject;

public class ChatSettingsScreenPresenterImpl extends BaseViewStateMvpPresenter<ChatSettingsScreen,
        ChatSettingsViewState> implements ChatSettingsScreenPresenter {

    private Activity activity;

    private Conversation conversation;
    private List<User> participants;

    public ChatSettingsScreenPresenterImpl(Activity activity, Intent startIntent) {
       // ((Injector)context.getApplicationContext()).inject(this);
        this.activity = activity;
        String conversationId = startIntent.getStringExtra(ChatActivity.EXTRA_CHAT_CONVERSATION_ID);
        conversation = new Select()
                .from(Conversation.class)
                .byIds(conversationId)
                .querySingle();
        String query = "SELECT * FROM Users u " +
                "JOIN ParticipantsRelationship p " +
                "ON p.userId = u._id " +
                "WHERE p.conversationId = ?";
        participants = SqlUtils.queryList(User.class, query, conversationId);
    }

    @Override
    public void onNewViewState() {
        state = new ChatSettingsViewState();
        state.setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
        getView().setConversation(conversation);
        getView().setParticipants(conversation, participants);
        getView().showContent();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
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

    public void onChangeChatImage() {
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
                return true;
            case R.id.action_edit_chat_name:
                onEditChatName();
                return true;
            case R.id.action_change_chat_image:
                onChangeChatImage();
                return true;
        }
        return false;
    }
}
