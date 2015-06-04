package com.worldventures.dreamtrips.modules.reptools.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoryDetailsFragment;

public class SuccessStoryDetailsProxyPresenter extends Presenter<Presenter.View> {

    private SuccessStory successStory;

    public SuccessStoryDetailsProxyPresenter(SuccessStory successStory) {
        this.successStory = successStory;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        Bundle bundle = new Bundle();
        bundle.putParcelable(SuccessStoryDetailsFragment.EXTRA_STORY, successStory);
        fragmentCompass.switchBranch(Route.SUCCESS_STORES_DETAILS, bundle);
    }

}
