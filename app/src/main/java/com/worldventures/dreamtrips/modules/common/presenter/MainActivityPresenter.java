package com.worldventures.dreamtrips.modules.common.presenter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.modules.common.view.util.LogoutDelegate;
import com.worldventures.dreamtrips.modules.gcm.service.RegistrationIntentService;

import javax.inject.Inject;

public class MainActivityPresenter extends ActivityPresenter<MainActivityPresenter.View> {

    @Inject
    protected RootComponentsProvider rootComponentsProvider;
    @Inject
    LogoutDelegate logoutDelegate;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        checkGoogleServices();
    }

    @Override
    public void onResume() {
        super.onResume();
        logoutDelegate.setDreamSpiceManager(dreamSpiceManager);
    }

    public void logout() {
        logoutDelegate.logout();
    }

    private void checkGoogleServices() {
        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (code != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(code, activity, 0).show();
        } else {
            activityRouter.startService(RegistrationIntentService.class);
        }
    }

    public interface View extends ActivityPresenter.View {

        void setTitle(int title);

        void makeActionBarGone(boolean hide);
    }
}
