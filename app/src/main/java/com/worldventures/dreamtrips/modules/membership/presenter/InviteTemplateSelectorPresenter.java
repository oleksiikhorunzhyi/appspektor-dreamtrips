package com.worldventures.dreamtrips.modules.membership.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class InviteTemplateSelectorPresenter extends Presenter {
    public InviteTemplateSelectorPresenter(View view) {
        super(view);
    }

    @Override
    public void resume() {
        super.resume();
        fragmentCompass.replace(Route.SELECT_INVITE_TEMPLATE);
    }
}
