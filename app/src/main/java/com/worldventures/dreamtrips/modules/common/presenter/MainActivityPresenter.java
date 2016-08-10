package com.worldventures.dreamtrips.modules.common.presenter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.common.api.janet.command.CirclesCommand;
import com.worldventures.dreamtrips.modules.gcm.service.RegistrationIntentService;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivityPresenter extends ActivityPresenter<MainActivityPresenter.View> {

    @Inject CirclesInteractor queryCirclesInteractor;
    @Inject FeatureManager featureManager;
    @Inject RootComponentsProvider rootComponentsProvider;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (featureManager.available(Feature.SOCIAL)) {
            queryCirclesInteractor.pipe().createObservable(new CirclesCommand())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindView())
                    .subscribe(new ActionStateSubscriber<CirclesCommand>()
                            .onSuccess(circlesCommand -> view.canShowView())
                            .onFail((circlesCommand, throwable) -> view.canShowView()));
        } else {
            view.canShowView();
        }
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

    public interface View extends ActivityPresenter.View {

        void canShowView();

        void setTitle(int title);

        void makeActionBarGone(boolean hide);
    }
}
