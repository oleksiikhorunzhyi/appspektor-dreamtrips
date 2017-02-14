package com.worldventures.dreamtrips.modules.video.view;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.modules.reptools.view.fragment.TrainingVideosFragment;
import com.worldventures.dreamtrips.modules.video.presenter.HelpVideosPresenter;

@Layout(R.layout.fragment_help_videos)
public class HelpVideosFragment extends TrainingVideosFragment<HelpVideosPresenter>
      implements HelpVideosPresenter.View, SelectablePagerFragment {

   @Override
   protected HelpVideosPresenter createPresenter(Bundle savedInstanceState) {
      return new HelpVideosPresenter();
   }

   @Override
   public void onSelectedFromPager() {
      getPresenter().onSelectedFromPager();
   }

}
