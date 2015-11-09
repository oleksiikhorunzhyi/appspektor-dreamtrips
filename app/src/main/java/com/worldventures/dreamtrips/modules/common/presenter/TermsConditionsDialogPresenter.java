package com.worldventures.dreamtrips.modules.common.presenter;

import android.content.Intent;
import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DeleteTokenGcmTask;
import com.worldventures.dreamtrips.modules.common.api.AcceptTermsConditionsCommand;
import com.worldventures.dreamtrips.modules.common.view.util.RouterHelper;
import com.worldventures.dreamtrips.modules.feed.api.UnsubscribeDeviceCommand;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;

import javax.inject.Inject;

public class TermsConditionsDialogPresenter extends Presenter<TermsConditionsDialogPresenter.View> {

    @Inject
    StaticPageProvider provider;
    @Inject
    SnappyRepository snappyRepository;

    private RouterHelper routerHelper;

    @Override
    public void takeView(View view) {
        super.takeView(view);

        routerHelper = new RouterHelper(activityRouter);

        view.loadContent(provider.getStaticInfoUrl(StaticInfoFragment.TERMS_TITLE));
    }

    public void acceptTerms(String text) {
        doRequest(new AcceptTermsConditionsCommand(text), aVoid -> view.dismissDialog());
    }

    public void logout() {
        String token = snappyRepository.getGcmRegToken();
        if (token != null) {
            doRequest(new UnsubscribeDeviceCommand(token), aVoid -> {
                deleteTokenInGcm();
            });
        } else {
            clearUserDataAndFinish();
        }
    }

    private void deleteTokenInGcm (){
        new DeleteTokenGcmTask(context, (task, removeGcmTokenSucceed) -> clearUserDataAndFinish()).execute();
    }

    private void clearUserDataAndFinish(){
        view.dismissDialog();
        snappyRepository.clearAll();
        appSessionHolder.destroy();
        routerHelper.logout();
    }

    public interface View extends Presenter.View {
        void loadContent(String url);

        void dismissDialog();
    }
}
