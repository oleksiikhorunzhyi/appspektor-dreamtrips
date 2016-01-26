package com.messenger.ui.presenter;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.messenger.delegate.ChatLeavingDelegate;
import com.messenger.di.MessengerStorageModule;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.OnChatLeftListener;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.ui.view.conversation.ConversationPath;
import com.messenger.ui.view.edit_member.EditChatPath;
import com.messenger.ui.view.settings.ChatSettingsScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.ui.viewstate.ChatSettingsViewState;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import flow.Flow;
import rx.Observable;

public abstract class ChatSettingsScreenPresenterImpl extends MessengerPresenterImpl<ChatSettingsScreen,
        ChatSettingsViewState> implements ChatSettingsScreenPresenter {

    String conversationId;
    Observable<Conversation> conversationObservable;
    Observable<List<User>> participantsObservable;

    protected final ChatLeavingDelegate chatLeavingDelegate;

    @Inject
    User user;
    @Inject
    MessengerServerFacade facade;

    @Inject
    ConversationsDAO conversationsDAO;
    @Inject
    ParticipantsDAO participantsDAO;

    @Inject
    @Named(MessengerStorageModule.DB_FLOW_RX_RESOLVER)
    RxContentResolver rxContentResolver;

    public ChatSettingsScreenPresenterImpl(Context context, String conversationId) {
        super(context);

        Injector injector = (Injector) context.getApplicationContext();
        injector.inject(this);

        chatLeavingDelegate = new ChatLeavingDelegate(injector, onChatLeftListener);

        this.conversationId = conversationId;
    }

    @Override
    public void onNewViewState() {
        state = new ChatSettingsViewState();
        state.setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
        getView().showContent();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        connectToConversation();

        // TODO Implement this
        // getView().setNotificationSettingStatus();
    }

    private void connectToConversation(){
        conversationObservable = conversationsDAO
                .getConversation(conversationId)
                .compose(new NonNullFilter<>())
                .first()
                .compose(bindViewIoToMainComposer())
                .replay(1)
                .autoConnect();

        conversationObservable.subscribe(conversation -> {
            connectToParticipants(conversation);

            ChatSettingsScreen screen = getView();
            screen.prepareViewForOwner(isUserOwner(conversation));
            screen.setConversation(conversation);
        });
    }

    private void connectToParticipants(Conversation conversation){
        participantsObservable = participantsDAO.getParticipants(conversationId)
                .onBackpressureLatest()
                .map(c -> SqlUtils.convertToList(User.class, c))
                .compose(bindViewIoToMainComposer());

        participantsObservable.subscribe(users ->
                getView().setParticipants(conversation, users));
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

    //////////////////////////////////////////////////////////////////////////
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
        conversationObservable.subscribe(conversation -> chatLeavingDelegate.leave(conversation));
    }

    private final OnChatLeftListener onChatLeftListener = new OnChatLeftListener() {
        @Override
        public void onChatLeft(String conversationId, String userId, boolean leave) {
            if (userId.equals(user.getId())) {
                Flow.get(getContext()).set(new ConversationPath());
            }
        }
    };

    @Override
    public void onNotificationsSwitchClicked(boolean isChecked) {

    }

    @Override
    public void onMembersRowClicked() {
        Flow.get(getContext()).set(new EditChatPath(conversationId));
    }

    public void onEditChatName() {
        conversationObservable
                .map(conversation -> conversation.getSubject())
                .subscribe(subject -> getView().showSubjectDialog(subject));
    }

    @Override
    public void onLeaveButtonClick() {
        conversationObservable
                .map(conversation -> conversation.getSubject())
                .subscribe(subject -> getView().showLeaveChatDialog(subject));
    }

    @Override
    public void applyNewChatSubject(String subject) {
        Observable<MultiUserChat> multiUserChatObservable = facade.getChatManager()
                .createMultiUserChatObservable(conversationId, facade.getOwner().getId())
                .flatMap(multiUserChat -> multiUserChat.setSubject(subject));

        Observable.zip(multiUserChatObservable, conversationObservable,
                (multiUserChat, conversation) -> conversation)
                .compose(new IoToMainComposer<>())
                .subscribe(conversation -> {
                    conversation.setSubject(subject);
                    conversation.save();
                    getView().setConversation(conversation);
                }, throwable -> {
                    getView().showErrorDialog(R.string.chat_settings_error_change_subject);
                });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Menu
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public int getToolbarMenuRes() {
        return R.menu.menu_chat_settings;
    }

    @Override
    public void onToolbarMenuPrepared(Menu menu) {
        conversationObservable.subscribe(conversation ->
                menu.findItem(R.id.action_overflow).setVisible(isUserOwner(conversation)));
    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
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

    ////////////////////////////////////////////////////
    ///// Helpers
    ////////////////////////////////////////////////////

    private boolean isUserOwner(Conversation conversation) {
        return conversation.getOwnerId() != null && conversation.getOwnerId().equals(user.getId());
    }
}
