package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.MemberImagesPresenter;

import butterknife.OnClick;

@Layout(R.layout.fragment_trip_list_images)
public class MemberImagesFragment extends TripImagesFragment<MemberImagesPresenter> {

   @OnClick(R.id.new_images_button)
   public void onShowNewImagesClick() {
      getPresenter().onShowNewImagesClick();
   }

   @Override
   protected MemberImagesPresenter createPresenter(Bundle savedInstanceState) {
      return new MemberImagesPresenter(getArgs());
   }
}
