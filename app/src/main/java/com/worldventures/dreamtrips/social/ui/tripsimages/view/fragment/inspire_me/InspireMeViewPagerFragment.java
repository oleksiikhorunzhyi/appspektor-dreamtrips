package com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.inspire_me;

import android.os.Bundle;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.inspire_me.InspireMeViewPagerPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.InspireMeViewPagerArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.BaseImageViewPagerFragment;

@Layout(R.layout.fragment_full_screen_photo_wrapper)
public class InspireMeViewPagerFragment extends BaseImageViewPagerFragment<InspireMeViewPagerPresenter, InspireMeViewPagerArgs>
      implements InspireMeViewPagerPresenter.View {

   @Override
   protected InspireMeViewPagerPresenter createPresenter(Bundle savedInstanceState) {
      return new InspireMeViewPagerPresenter(getArgs());
   }
}
