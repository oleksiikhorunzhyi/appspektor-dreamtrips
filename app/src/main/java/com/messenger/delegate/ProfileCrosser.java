package com.messenger.delegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.messenger.entities.User;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;


public class ProfileCrosser {

    Context context;
    RouteCreator profileRouteCreator;

    public ProfileCrosser(Context context, RouteCreator profileRouteCreator) {
        this.context = context;
        this.profileRouteCreator = profileRouteCreator;
    }

    public void crossToProfile(User user) {
        com.worldventures.dreamtrips.modules.common.model.User socialUser =
                new com.worldventures.dreamtrips.modules.common.model.User(user.getSocialId());

        Intent result = new Intent(context, ComponentActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(ComponentPresenter.ROUTE, profileRouteCreator.createRoute(socialUser.getId()));
        args.putSerializable(ComponentPresenter.COMPONENT_TOOLBAR_CONFIG,
                ToolbarConfig.Builder.create().visible(false).build());
        args.putParcelable(ComponentPresenter.EXTRA_DATA, new UserBundle(socialUser));
        result.putExtra(ComponentPresenter.COMPONENT_EXTRA, args);

        context.startActivity(result);
    }
}
