package com.worldventures.dreamtrips.modules.common.presenter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.worldventures.dreamtrips.core.utils.events.ServerDownEvent;
import com.worldventures.dreamtrips.core.utils.events.UpdateRegionsAndThemesEvent;

public class MainActivityPresenter extends ActivityPresenter<MainActivityPresenter.View> {

    @Override
    public void takeView(View view) {
        super.takeView(view);
        checkGoogleServices();
        loadFilters();
        setCurrentComponentTitle();
    }

    private void checkGoogleServices() {
        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (code != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(code, activity, 0).show();
        }
    }

    public void loadFilters() {
        eventBus.post(new UpdateRegionsAndThemesEvent());
    }

    private void setCurrentComponentTitle() {
        view.setTitle(fragmentCompass.getCurrentState().getTitle());
    }

    public void onEvent(ServerDownEvent event) {
        view.alert(event.getMessage());
    }

    public interface View extends Presenter.View {
        void setTitle(int title);
    }
}
