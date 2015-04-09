package com.worldventures.dreamtrips.modules.common.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.UpdateSelectionEvent;

public class MainActivityPresenter extends ActivityPresenter<MainActivityPresenter.View> {

    public MainActivityPresenter(View view) {
        super(view);
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
