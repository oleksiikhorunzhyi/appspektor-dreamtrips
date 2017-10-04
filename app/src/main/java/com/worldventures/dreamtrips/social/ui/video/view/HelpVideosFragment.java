package com.worldventures.dreamtrips.social.ui.video.view;

import android.os.Bundle;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.reptools.view.fragment.TrainingVideosFragment;
import com.worldventures.dreamtrips.social.ui.video.presenter.HelpVideosPresenter;

@Layout(R.layout.fragment_help_videos)
public class HelpVideosFragment extends TrainingVideosFragment<HelpVideosPresenter>
      implements HelpVideosPresenter.View {

   @Override
   protected HelpVideosPresenter createPresenter(Bundle savedInstanceState) {
      return new HelpVideosPresenter();
   }

   @Override
   public void trackViewFromViewPagerIfNeeded() {
      getPresenter().track();
   }
}
