package com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment;

import android.os.Bundle;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.MemberImagesPresenter;

import butterknife.OnClick;

@Layout(R.layout.fragment_trip_list_images)
public class MemberImagesFragment extends TripImagesFragment<MemberImagesPresenter> {

   @OnClick(R.id.new_images_button)
   public void onShowNewImagesClick() {
      recyclerView.scrollToPosition(0);
      getPresenter().onShowNewImagesClick();
   }

   @Override
   protected MemberImagesPresenter createPresenter(Bundle savedInstanceState) {
      return new MemberImagesPresenter(getArgs());
   }
}
