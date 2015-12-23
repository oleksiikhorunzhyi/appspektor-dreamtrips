package com.messenger.synchmechanism;

import android.content.Context;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.session.UserSession;

public class MessengerConnector {

    private static MessengerConnector object;

    private Context applicationContext;

    private SessionHolder<UserSession> appSessionHolder;
    private MessengerServerFacade messengerServerFacade;
    private DreamSpiceManager spiceManager;
    private CacheSynchronizer cacheSynchronizer;

    private MessengerConnector(Context applicationContext, SessionHolder<UserSession> appSessionHolder,
                               MessengerServerFacade messengerServerFacade, DreamSpiceManager spiceManager) {
        this.applicationContext = applicationContext;
        this.appSessionHolder = appSessionHolder;
        this.messengerServerFacade = messengerServerFacade;
        this.spiceManager = spiceManager;
        this.cacheSynchronizer = new CacheSynchronizer(messengerServerFacade, spiceManager);
    }

    public static MessengerConnector getInstance() {
        if (object == null) {
            throw new IllegalStateException("You should initialize it");
        }
        return object;
    }

    public static void init(Context applicationContext, SessionHolder<UserSession> appSessionHolder,
                            MessengerServerFacade messengerServerFacade, DreamSpiceManager spiceManager) {
        object = new MessengerConnector(applicationContext, appSessionHolder, messengerServerFacade, spiceManager);
    }

    public void connect() {
        if (messengerServerFacade.isAuthorized() || appSessionHolder == null ||
                appSessionHolder.get() == null || !appSessionHolder.get().isPresent()) {
            return;
        }

        UserSession userSession = appSessionHolder.get().get();
        messengerServerFacade.addAuthorizationListener(new AuthorizeListener() {
            @Override
            public void onSuccess() {
                spiceManager.start(applicationContext);
                cacheSynchronizer.updateCache(messengerServerFacade::setPresenceStatus);
            }
        });
        messengerServerFacade.authorizeAsync(userSession.getUsername(), userSession.getLegacyApiToken());
    }

    public void disconnect() {
        if (messengerServerFacade.isAuthorized()) {
            spiceManager.shouldStop();
            messengerServerFacade.disconnectAsync();
            cacheSynchronizer.clearCache();
        }
    }

}
