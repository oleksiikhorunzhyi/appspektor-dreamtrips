package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.messenger.delegate.ChatLeavingDelegate;
import com.messenger.di.MessengerStorageModule;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.OnChatLeftListener;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.activity.EditChatMembersActivity;
import com.messenger.ui.activity.MessengerStartActivity;
import com.messenger.ui.view.ChatSettingsScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.ui.viewstate.ChatSettingsViewState;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class ChatSettingsScreenPresenterImpl extends MessengerPresenterImpl<ChatSettingsScreen,
        ChatSettingsViewState> implements ChatSettingsScreenPresenter {

    protected Activity activity;

    protected Conversation conversation;
    protected List<User> participants;

    protected final ChatLeavingDelegate chatLeavingDelegate;

    @Inject
    User user;
    @Inject
    MessengerServerFacade facade;
    @Inject
    @Named(MessengerStorageModule.DB_FLOW_RX_RESOLVER)
    RxContentResolver rxContentResolver;

    public ChatSettingsScreenPresenterImpl(Activity activity, Intent startIntent) {
        this.activity = activity;
        String conversationId = startIntent.getStringExtra(ChatActivity.EXTRA_CHAT_CONVERSATION_ID);
        Injector injector = (Injector) activity.getApplication();
        injector.inject(this);

        conversation = ConversationsDAO.getConversationById(conversationId);
        chatLeavingDelegate = new ChatLeavingDelegate(injector, onChatLeftListener);
    }

    @Override
    public void onNewViewState() {
        state = new ChatSettingsViewState();
        state.setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
        getView().setConversation(conversation);
        getView().showContent();
    }

    private boolean isUserOwner() {
        return conversation.getOwnerId() != null && conversation.getOwnerId().equals(user.getId());
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getView().prepareViewForOwner(isUserOwner());
        ParticipantsDAO.selectParticipants(rxContentResolver, conversation.getId(),
                User.CONTENT_URI, ParticipantsRelationship.CONTENT_URI)
                .onBackpressureLatest()
                .map(c -> SqlUtils.convertToList(User.class, c))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindVisibility())
                .subscribe(users -> {
                    participants = users;
                    getView().setParticipants(conversation, users);
                });

        // TODO Implement this
        // getView().setNotificationSettingStatus();
    }

    @Override
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
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            chatLeavingDelegate.register();
        } else {
            chatLeavingDelegate.unregister();
        }
    }

    @Override
    public void onClearChatHistoryClicked() {
    }

    @Override
    public void onLeaveChatClicked() {
        chatLeavingDelegate.leave(conversation);
    }

    private final OnChatLeftListener onChatLeftListener = new OnChatLeftListener() {
        @Override
        public void onChatLeft(String conversationId, String userId) {
            if (userId.equals(user.getId())) {
                Intent intent = new Intent(activity, MessengerStartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
            }
        }
    };

    @Override
    public void onNotificationsSwitchClicked(boolean isChecked) {

    }

    @Override
    public void onMembersRowClicked() {
        EditChatMembersActivity.start(activity, conversation.getId());
    }

    @Override
    public String getCurrentSubject() {
        return conversation.getSubject();
    }

    public void onEditChatName() {
        getView().showSubjectDialog();
    }

    @Override
    public void applyNewChatSubject(String subject) {
        facade.getChatManager().createMultiUserChatObservable(conversation.getId(), facade.getOwner().getId())
                .flatMap(multiUserChat -> multiUserChat.setSubject(subject))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(multiUserChat1 -> {
                    conversation.setSubject(subject);
                    conversation.save();
                    getView().setConversation(conversation);
                }, throwable -> {
                    getView().showErrorDialog(R.string.chat_settings_error_change_subject);
                });
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
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_overflow).setVisible(isUserOwner());
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
