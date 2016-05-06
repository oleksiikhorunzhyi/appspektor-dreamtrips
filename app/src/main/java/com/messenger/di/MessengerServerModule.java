package com.messenger.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.XmppServerParams;
import com.messenger.messengerservers.xmpp.providers.GsonAttachmentAdapter;
import com.worldventures.dreamtrips.BuildConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class MessengerServerModule {

    @Singleton
    @Provides
    MessengerServerFacade provideXmppServerFacade() {
        Gson gson = new GsonBuilder().registerTypeAdapter(AttachmentHolder.class, new GsonAttachmentAdapter()).create();
        return new XmppServerFacade(
                new XmppServerParams(BuildConfig.MESSENGER_API_URL, BuildConfig.MESSENGER_API_PORT), gson);
    }
}
