package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.view.fragment.reptools.SuccessStoriesDetailsFragment;

public class SuccessStoryDetailsActivityPM extends BasePresentation<BasePresentation.View> {

    public SuccessStoryDetailsActivityPM(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
    }

    public void onCreate(SuccessStory story) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SuccessStoriesDetailsFragment.STORY, story);
        fragmentCompass.add(State.SUCCESS_STORES_DETAILS, bundle);
    }
}
