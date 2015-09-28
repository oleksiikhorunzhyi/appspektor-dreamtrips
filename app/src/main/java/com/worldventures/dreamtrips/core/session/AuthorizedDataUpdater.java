package com.worldventures.dreamtrips.core.session;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.friends.api.GetCirclesQuery;

import javax.inject.Inject;

import timber.log.Timber;

public class AuthorizedDataUpdater {

    @Inject
    SessionHolder<UserSession> appSessionHolder;
    @Inject
    SnappyRepository repository;
    @Inject
    FeatureManager featureManager;

    public AuthorizedDataUpdater(Injector injector) {
        injector.inject(this);
    }

    public void updateData(DreamSpiceManager requestManager) {
        if (!appSessionHolder.get().isPresent()) {
            throw new IllegalStateException("User is not logged in");
        }
        Timber.d("Loading authorized data");
        checkCircles(requestManager);
    }

    private void checkCircles(DreamSpiceManager requestManager) {
        if (!featureManager.available(Feature.SOCIAL)) return;
        //
        requestManager.execute(new GetCirclesQuery(repository),
                (result) -> done(),
                e -> Timber.w(e, "Can't update authorized data"));
    }

    private void done() {
        Timber.d("Authorized data loaded");
    }
}
