package com.worldventures.dreamtrips.modules.common.presenter;

import android.text.TextUtils;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.ServerDownEvent;
import com.worldventures.dreamtrips.core.utils.events.UpdateSelectionEvent;
import com.worldventures.dreamtrips.modules.auth.model.LoginResponse;

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

    public void onEvent(ServerDownEvent event) {
        view.alert(event.getMessage());
    }

    public static interface View extends Presenter.View {
        void setTitle(int title);
    }
}
