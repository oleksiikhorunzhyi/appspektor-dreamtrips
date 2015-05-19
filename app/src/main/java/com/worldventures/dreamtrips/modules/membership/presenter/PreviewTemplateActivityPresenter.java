package com.worldventures.dreamtrips.modules.membership.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;


public class PreviewTemplateActivityPresenter extends Presenter {

    private String url;

    public PreviewTemplateActivityPresenter(String url) {
        super();
        this.url = url;
    }

    public void showPreview() {
        Bundle bundle = new Bundle();
        bundle.putString(StaticInfoFragment.BundleUrlFragment.URL_EXTRA, url);
        fragmentCompass.switchBranch(Route.PREVIEW_TEMPLATE, bundle);
    }

}
