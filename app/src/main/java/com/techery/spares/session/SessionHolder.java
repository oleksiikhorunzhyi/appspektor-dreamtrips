package com.techery.spares.session;

import com.techery.spares.storage.complex_objects.ComplexObjectStorage;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;

import de.greenrobot.event.EventBus;

public class SessionHolder<S> extends ComplexObjectStorage<S> {

    public interface Events {
        class SessionChanged {

        }

        class SessionCreated<SESSION_CLASS> extends SessionChanged {
            private final SESSION_CLASS session;

            public SessionCreated(SESSION_CLASS session) {
                this.session = session;
            }

            public SESSION_CLASS getSession() {
                return session;
            }
        }

        class SessionDestroyed extends SessionChanged {

        }
    }

    private final EventBus eventBus;

    public SessionHolder(SimpleKeyValueStorage keyValueStorage, Class<S> cls, EventBus bus) {
        super(keyValueStorage, "SESSION_KEY", cls);
        this.eventBus = bus;
    }

    public void put(S session) {
        super.put(session);
        this.eventBus.post(new Events.SessionCreated<>(session));
    }
}
