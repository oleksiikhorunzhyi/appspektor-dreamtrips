package com.worldventures.dreamtrips.modules.common.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.utils.events.OpenMenuItemEvent;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

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

    public void onEvent(OpenMenuItemEvent event) {
        openComponent(event.getComponentDescription(), event.getArgs());
        view.updateSelection(event.getComponentDescription());
    }

    private void setCurrentComponentTitle() {
        view.setTitle(fragmentCompass.getCurrentState().getTitle());
    }

    public void openComponent(ComponentDescription component, @Nullable Bundle args) {
        Fragment currentFragment = fragmentCompass.getCurrentFragment();
        boolean theSame = currentFragment != null && currentFragment.getClass().equals(component.getFragmentClass());
        if (!theSame) {
            fragmentCompass.replace(component, args);
            view.setTitle(component.getTitle());
        }
    }

    public void openComponent(ComponentDescription component) {
        openComponent(component, null);
    }

    public interface View extends Presenter.View {
        void setTitle(int title);

        void updateSelection(ComponentDescription componentDescription);
    }
}
