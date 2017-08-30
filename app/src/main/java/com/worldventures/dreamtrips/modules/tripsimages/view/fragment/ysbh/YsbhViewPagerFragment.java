package com.worldventures.dreamtrips.modules.tripsimages.view.fragment.ysbh;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.ysbh.YsbhViewPagerPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.YsbhPagerArgs;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.BaseImageViewPagerFragment;

@Layout(R.layout.fragment_full_screen_photo_wrapper)
public class YsbhViewPagerFragment extends BaseImageViewPagerFragment<YsbhViewPagerPresenter, YsbhPagerArgs>
      implements YsbhViewPagerPresenter.View {

   @Override
   protected YsbhViewPagerPresenter createPresenter(Bundle savedInstanceState) {
      return new YsbhViewPagerPresenter(getArgs());
   }
}
