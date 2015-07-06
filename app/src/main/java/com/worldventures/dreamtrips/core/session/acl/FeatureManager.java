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

    public void checkIf(@Feature.FeatureName String name, OnFeatureChecked onFeatureChecked) {
        onFeatureChecked.onChecked(available(name));
    }

    public void with(@Feature.FeatureName String name, OnFeatureAvailable onAvailable) {
        with(name, onAvailable, null);
    }

    public void without(@Feature.FeatureName String name, OnFeatureMissing onMissing) {
        with(name, null, onMissing);
    }

    public void with(@Feature.FeatureName String name, OnFeatureAvailable onAvailable, OnFeatureMissing onMissing) {
        if (available(name)) {
            if (onAvailable != null) onAvailable.onAvailable();
        } else {
            if (onMissing != null) onMissing.onMissing();
        }
    }

    public interface OnFeatureChecked {
        void onChecked(boolean isAvailable);
    }

    public interface OnFeatureAvailable {
        void onAvailable();
    }

    public interface OnFeatureMissing {
        void onMissing();
    }

}
