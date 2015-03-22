package com.worldventures.dreamtrips.modules.reptools.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.SuccessStoriesDetailsFragment;

public class SuccessStoryDetailsPresenter extends Presenter<Presenter.View> {

    public SuccessStoryDetailsPresenter(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
    }

    public void onCreate(SuccessStory story) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SuccessStoriesDetailsFragment.STORY, story);
        fragmentCompass.add(Route.SUCCESS_STORES_DETAILS, bundle);
    }
}
