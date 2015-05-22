package com.worldventures.dreamtrips.modules.reptools.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoriesDetailsFragment;

public class SuccessStoryDetailsPresenter extends Presenter<Presenter.View> {

    @Override
    public void takeView(View view) {
        super.takeView(view);
    }

    public void onCreate(SuccessStory story) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SuccessStoriesDetailsFragment.EXTRA_STORY, story);
        fragmentCompass.add(Route.SUCCESS_STORES_DETAILS, bundle);
    }
}
