package com.messenger.initializer;

import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.util.ChatFacadeManager;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

import timber.log.Timber;

public class ChatFacadeInitializer implements AppInitializer {

    @Inject
    MessengerServerFacade messengerServerFacade;
    //
    @Inject
    ChatFacadeManager chatFacadeManager;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        //
        GlobalEventEmitter emitter = messengerServerFacade.getGlobalEventEmitter();
        //
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
        });

        emitter.addOnSubjectChangesListener((conversationId, subject) ->
                chatFacadeManager.onSubjectChanged(conversationId, subject));

        emitter.addOnAvatarChangeListener((conversationId, subject) ->
                chatFacadeManager.onAvatarChanged(conversationId, subject));

        emitter.addInvitationListener((conversationId) -> {
            Timber.i("Chat invited :: chat=%s", conversationId);
            chatFacadeManager.onChatInvited(conversationId);
        });

        chatFacadeManager.processJoinedEvents(emitter.createChatJoinedObservable());

        emitter.addOnChatLeftListener((conversationId, userId, leave) ->
                chatFacadeManager.onChatLeft(conversationId, userId, leave));
    }

}
