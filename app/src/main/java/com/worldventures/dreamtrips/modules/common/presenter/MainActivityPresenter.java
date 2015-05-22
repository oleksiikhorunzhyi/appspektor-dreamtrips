package com.worldventures.dreamtrips.modules.common.presenter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivityPresenter extends ActivityPresenter<MainActivityPresenter.View> {

    @Override
    public void takeView(View view) {
        super.takeView(view);
        checkGoogleServices();
        setCurrentComponentTitle();
    }

    private void checkGoogleServices() {
        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (code != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(code, activity, 0).show();
        }
    }

    private void setCurrentComponentTitle() {
        view.setTitle(fragmentCompass.getCurrentState().getTitle());
    }

    public interface View extends Presenter.View {
        void setTitle(int title);
    }
}
