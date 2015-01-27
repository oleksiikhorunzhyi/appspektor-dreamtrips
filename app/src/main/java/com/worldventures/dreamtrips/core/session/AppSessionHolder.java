package com.worldventures.dreamtrips.core.session;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;

import de.greenrobot.event.EventBus;

public class AppSessionHolder extends SessionHolder<UserSession> {

    public AppSessionHolder(SimpleKeyValueStorage keyValueStorage, EventBus bus) {
        super(keyValueStorage, UserSession.class, bus);
    }
}
