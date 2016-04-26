package com.messenger.di;

import android.content.Context;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.XmppServerParams;
import com.messenger.storage.dao.UsersDAO;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;

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
    MessengerServerFacade provideXmppServerFacade(@ForApplication Context context, DreamSpiceManager requester, UsersDAO usersDAO) {
        return new XmppServerFacade(
                new XmppServerParams(BuildConfig.MESSENGER_API_URL, BuildConfig.MESSENGER_API_PORT));
    }
}
