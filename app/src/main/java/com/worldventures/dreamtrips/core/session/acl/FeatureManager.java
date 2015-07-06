package com.worldventures.dreamtrips.core.session.acl;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

public class FeatureManager {

    private SessionHolder<UserSession> sessionHolder;

    public FeatureManager(SessionHolder<UserSession> sessionHolder) {
        this.sessionHolder = sessionHolder;
    }

    public boolean available(@Feature.FeatureName String name) {
        UserSession userSession = sessionHolder.get().orNull();
        if (userSession == null || userSession.getFeatures() == null) return false;
        return Queryable.from(userSession.getFeatures()).any(f -> f.name.equals(name));
    }
}
