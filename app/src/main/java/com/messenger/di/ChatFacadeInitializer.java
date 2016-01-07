package com.messenger.di;

import com.messenger.delegate.LoaderDelegate;
import com.messenger.delegate.UserProcessor;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.storege.dao.ConversationsDAO;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;

import java.util.Date;

import javax.inject.Inject;

public class ChatFacadeInitializer implements AppInitializer {

    @Inject
    MessengerServerFacade messengerServerFacade;

    @Inject
    ConversationsDAO conversationsDAO;

    @Inject
    DreamSpiceManager spiceManager;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);

        GlobalEventEmitter emiter = messengerServerFacade.getGlobalEventEmitter();
        emiter.addGlobalMessageListener(new GlobalMessageListener() {
            @Override
            public void onReceiveMessage(Message message) {
                conversationsDAO.incrementUnreadField(message.getConversationId());
                message.setDate(new Date());
                message.setRead(false);
                message.save();
            }

            @Override
            public void onSendMessage(Message message) {
                message.setDate(new Date());
                message.setRead(true);
                ContentUtils.insert(Message.CONTENT_URI, message);
            }
        });

        emiter.addOnSubjectChangesListener((conversationId, subject) -> conversationsDAO.getConversation(conversationId)
                .first()
                .subscribe(conversation -> {
                    if (conversation == null)
                        return; // TODO there should be no such situation, but sync init state is broken
                    conversation.setSubject(subject);
                    conversation.save();
                }));

        emiter.addOnChatCreatedListener((conversationId, createLocally) ->
                        conversationsDAO.getConversation(conversationId)
                                .first()
                                .filter(conversation -> conversation == null)
                                .flatMap(conversation -> {
                                    LoaderDelegate loaderDelegate = new LoaderDelegate(messengerServerFacade, new UserProcessor(spiceManager));
                                    return loaderDelegate.loadConversations();
                                })
                                .subscribe()

        );
    }
}
