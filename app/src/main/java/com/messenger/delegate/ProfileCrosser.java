package com.messenger.delegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.messenger.entities.DataUser;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Named;

import static com.worldventures.dreamtrips.core.module.RouteCreatorModule.PROFILE;


public class ProfileCrosser {

    private final WeakReference<Context> weakReferenceContext;
    private final RouteCreator<Integer> routeCreator;

    @Inject
    public ProfileCrosser(@ForActivity Context context, @Named(PROFILE) RouteCreator<Integer> routeCreator) {
        this.weakReferenceContext = new WeakReference<>(context);
        this.routeCreator = routeCreator;
    }

    public void crossToProfile(DataUser user) {
        Context context = weakReferenceContext.get();
        if (context == null) return;

        com.worldventures.dreamtrips.modules.common.model.User socialUser =
                new com.worldventures.dreamtrips.modules.common.model.User(user.getSocialId());

        Intent result = new Intent(context, ComponentActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(ComponentPresenter.ROUTE, routeCreator.createRoute(socialUser.getId()));
        args.putSerializable(ComponentPresenter.COMPONENT_TOOLBAR_CONFIG,
                ToolbarConfig.Builder.create().visible(false).build());
        args.putParcelable(ComponentPresenter.EXTRA_DATA, new UserBundle(socialUser));
        result.putExtra(ComponentPresenter.COMPONENT_EXTRA, args);

        context.startActivity(result);
    }
}
