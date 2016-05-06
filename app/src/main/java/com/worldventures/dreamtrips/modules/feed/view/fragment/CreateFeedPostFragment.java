package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateFeedPostPresenter;

@Layout(R.layout.layout_post)
public class CreateFeedPostFragment extends CreateEntityFragment<CreateFeedPostPresenter> implements CreateFeedPostPresenter.View {

    @Override
    protected CreateFeedPostPresenter createPresenter(Bundle savedInstanceState) {
        return new CreateFeedPostPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArgs() != null && getArgs().isShowPickerImmediately()) {
            showMediaPicker();
            getArgs().setShowPickerImmediately(false);
        }
        updatePickerState();
    }

    @Override
    protected Route getRoute() {
        return Route.POST_CREATE;
    }
}
