package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;

@Layout(R.layout.activity_launch)
public class LaunchActivity extends ActivityWithPresenter<LaunchActivityPresenter> {

    @Override
    protected LaunchActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new LaunchActivityPresenter(this);
    }

}
