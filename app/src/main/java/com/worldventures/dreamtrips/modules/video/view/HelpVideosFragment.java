package com.worldventures.dreamtrips.modules.video.view;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.reptools.presenter.TrainingVideosPresenter;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.TrainingVideosFragment;
import com.worldventures.dreamtrips.modules.video.presenter.HelpVideosPresenter;

public class HelpVideosFragment extends TrainingVideosFragment {

    @Override
    protected TrainingVideosPresenter createPresenter(Bundle savedInstanceState) {
        return new HelpVideosPresenter();
    }
}
