package com.messenger.di;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import java.util.Date;

import javax.inject.Inject;

public class ChatFacadeInitializer implements AppInitializer {

    @Inject
    MessengerServerFacade messengerServerFacade;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);

        messengerServerFacade.getGlobalEventEmitter().addGlobalMessageListener(new GlobalMessageListener() {
            @Override
            public void onReceiveMessage(Message message) {
                message.setDate(new Date());
                ContentUtils.insert(Message.CONTENT_URI, message);
            }

            @Override
            public void onSendMessage(Message message) {
                message.setDate(new Date());
                ContentUtils.insert(Message.CONTENT_URI, message);
            }
        });
    }
}
