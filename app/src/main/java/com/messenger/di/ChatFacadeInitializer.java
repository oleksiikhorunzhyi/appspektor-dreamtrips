package com.messenger.di;

import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.selectable.SelectableCell;

import java.util.Date;

import javax.inject.Inject;

public class ChatFacadeInitializer implements AppInitializer {

    @Inject
    MessengerServerFacade messengerServerFacade;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);

        GlobalEventEmitter emiter = messengerServerFacade.getGlobalEventEmitter();
        emiter.addGlobalMessageListener(new GlobalMessageListener() {
            @Override
            public void onReceiveMessage(Message message) {
                message.setDate(new Date());
                message.setRead(false);
                ContentUtils.insert(Message.CONTENT_URI, message);
            }

            @Override
            public void onSendMessage(Message message) {
                message.setDate(new Date());
                message.setRead(true);
                ContentUtils.insert(Message.CONTENT_URI, message);
            }
        });

        emiter.addOnSubjectChangesListener((conversationId, subject) -> {
            final Conversation conversation = new Select().from(Conversation.class).byIds(conversationId).querySingle();
            conversation.setSubject(subject);
            conversation.save();
        });
    }
}
