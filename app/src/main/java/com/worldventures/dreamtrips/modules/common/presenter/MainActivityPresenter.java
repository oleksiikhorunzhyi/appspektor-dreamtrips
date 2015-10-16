package com.worldventures.dreamtrips.modules.common.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.utils.events.ActionBarHideEvent;
import com.worldventures.dreamtrips.modules.gcm.service.RegistrationIntentService;

import javax.inject.Inject;

public class MainActivityPresenter extends ActivityPresenter<MainActivityPresenter.View> {

    @Inject
    protected RootComponentsProvider rootComponentsProvider;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        checkGoogleServices();
    }

    private void checkGoogleServices() {
        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (code != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(code, activity, 0).show();
        } else {
            activityRouter.startService(RegistrationIntentService.class);
        }
    }

    public void onEvent(ActionBarHideEvent event) {
        view.makeActionBarGone(event.isHidden());
    }

    public void openComponent(ComponentDescription component, @Nullable Bundle args) {
        view.setTitle(component.getToolbarTitle());
        Fragment currentFragment = fragmentCompass.getCurrentFragment();
        // check if current
        boolean theSame = currentFragment != null && currentFragment.getClass().equals(component.getFragmentClass());
        if (theSame) return;
        // check if in stack
        String backStackName = null;
        FragmentManager fm = fragmentCompass.getFragmentManager();
        for (int entry = 0; entry < fm.getBackStackEntryCount(); entry++) {
            String name = fm.getBackStackEntryAt(entry).getName();
            if (name.equals(component.getKey())) {
                backStackName = name;
                break;
            }
        }
        if (backStackName != null) {
            fm.popBackStack(backStackName, 0);
            return;
        }
        fragmentCompass.replace(component, args);
    }

    public void openComponent(ComponentDescription component) {
        openComponent(component, null);
    }

    public interface View extends Presenter.View {
        void setTitle(int title);

        void updateSelection(ComponentDescription componentDescription);

        void makeActionBarGone(boolean isTransparent);
    }
}
