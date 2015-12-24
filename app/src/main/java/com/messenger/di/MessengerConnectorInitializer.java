package com.messenger.di;

import android.content.Context;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.synchmechanism.MessengerConnector;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Inject;

public class MessengerConnectorInitializer implements AppInitializer {

    @Inject
    @ForApplication
    Context context;
    @Inject
    SessionHolder<UserSession> appSessionHolder;
    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    DreamSpiceManager spiceManager;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        MessengerConnector.init(context, appSessionHolder, messengerServerFacade, spiceManager);
    }
}
