package com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.ysbh;

import android.os.Bundle;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh.YSBHViewPagerPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.YsbhPagerArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.BaseImageViewPagerFragment;

@Layout(R.layout.fragment_full_screen_photo_wrapper)
public class YsbhViewPagerFragment extends BaseImageViewPagerFragment<YSBHViewPagerPresenter, YsbhPagerArgs>
      implements YSBHViewPagerPresenter.View {

   @Override
   protected YSBHViewPagerPresenter createPresenter(Bundle savedInstanceState) {
      return new YSBHViewPagerPresenter(getArgs());
   }
}
