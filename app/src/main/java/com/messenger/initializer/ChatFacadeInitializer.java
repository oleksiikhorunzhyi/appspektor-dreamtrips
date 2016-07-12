package com.messenger.initializer;

import com.messenger.delegate.chat.typing.TypingManager;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.ChatState;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.util.ChatFacadeManager;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class ChatFacadeInitializer implements AppInitializer {
    @Inject MessengerServerFacade messengerServerFacade;
    @Inject ChatFacadeManager chatFacadeManager;
    @Inject TypingManager typingManager;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);

        GlobalEventEmitter emitter = messengerServerFacade.getGlobalEventEmitter();

        emitter.addGlobalMessageListener(new GlobalMessageListener() {
            @Override
            public void onReceiveMessage(Message message) {
                chatFacadeManager.onReceivedMessage(message);
            }

            @Override
            public void onPreSendMessage(Message message) {
                chatFacadeManager.onPreSendMessage(message);
            }

            @Override
            public void onSendMessage(Message message) {
                chatFacadeManager.onSendMessage(message);
            }

            @Override
            public void onErrorMessage(Message message) {
                chatFacadeManager.onErrorMessage(message);
            }
        });
        emitter.addOnChatStateChangedListener(this::handleChatState);

        chatFacadeManager.processJoinedEvents(emitter.createChatJoinedObservable());
        emitter.addOnMessagesDeletedListener(chatFacadeManager::onMessagesDeleted);
        emitter.addOnSubjectChangesListener(chatFacadeManager::onSubjectChanged);
        emitter.addOnAvatarChangeListener(chatFacadeManager::onAvatarChanged);
        emitter.addInvitationListener(chatFacadeManager::onChatInvited);
        emitter.addOnChatLeftListener(chatFacadeManager::onChatLeft);
        emitter.addOnKickListener(chatFacadeManager::onKicked);
        emitter.addOnClearChatEventListener(chatFacadeManager::onClearChat);
        emitter.addOnRevertClearingEventListener(chatFacadeManager::onRevertClearing);
    }

    public void handleChatState(String conversationId, String userId, @ChatState.State String chatState) {
        switch (chatState) {
            case ChatState.COMPOSING:
                typingManager.userStartTyping(conversationId, userId);
                break;
            case ChatState.PAUSE:
                typingManager.userStopTyping(conversationId, userId);
                break;
        }
    }
}
