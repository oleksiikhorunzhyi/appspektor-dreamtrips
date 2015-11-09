package com.worldventures.dreamtrips.modules.common.view.util;

import android.content.Intent;
import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;

public class RouterHelper {

    private ActivityRouter activityRouter;

    public RouterHelper(ActivityRouter activityRouter) {
        this.activityRouter = activityRouter;
    }

    public void logout() {
        Bundle args = new Bundle();
        args.putSerializable(ComponentPresenter.COMPONENT_TOOLBAR_CONFIG, ToolbarConfig.Builder.create().visible(false).build());
        activityRouter.openComponentActivity(Route.LOGIN, args,
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
