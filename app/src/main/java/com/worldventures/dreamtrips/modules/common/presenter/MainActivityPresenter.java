package com.worldventures.dreamtrips.modules.common.presenter;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.UpdateSelectionEvent;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class MainActivityPresenter extends ActivityPresenter<MainActivityPresenter.View> {

    @Global
    @Inject
    EventBus eventBus;

    public MainActivityPresenter(View view) {
        super(view);
    }

    public void create() {

    }

    @Override
    public void resume() {
        super.resume();
        updateFaqAndTermLinks();
    }

    private void updateFaqAndTermLinks() {

    }

    public void onBackPressed() {
        Route currentRoute = fragmentCompass.getPreviousFragment();
        int title = currentRoute.getTitle();
        eventBus.post(new UpdateSelectionEvent());
        view.setTitle(title);
    }

    public void restoreInstanceState() {
        view.setTitle(fragmentCompass.getCurrentState().getTitle());
    }

    public static interface View extends Presenter.View {
        void setTitle(int title);
    }
}
