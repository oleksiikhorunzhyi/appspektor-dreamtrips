package com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.YSBHPhoto;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.YouShouldBeHerePhotoFullscreenPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_fullscreen_ysbh_photo)
public class YSBHPhotoFullscreenFragment extends FullScreenPhotoFragment<YouShouldBeHerePhotoFullscreenPresenter, YSBHPhoto> implements YouShouldBeHerePhotoFullscreenPresenter.View {

   @InjectView(R.id.tv_description) TextView tvDescription;
   @InjectView(R.id.tv_see_more) TextView tvSeeMore;
   @InjectView(R.id.iv_share) ImageView ivShare;
   @InjectView(R.id.ll_more_info) LinearLayout llMoreInfo;

   @Override
   protected YouShouldBeHerePhotoFullscreenPresenter createPresenter(Bundle savedInstanceState) {
      return new YouShouldBeHerePhotoFullscreenPresenter((YSBHPhoto) getArgs().getPhoto(), getArgs().getType());
   }

   @Override
   public void setContent(IFullScreenObject photo) {
      super.setContent(photo);
      tvDescription.setText(photo.getFSDescription());
   }

   @OnClick(R.id.tv_see_more)
   protected void actionSeeMore() {
      llMoreInfo.setVisibility(View.VISIBLE);
      tvDescription.setSingleLine(false);

      tvSeeMore.setVisibility(View.GONE);
      if (tvDescription.getText().length() == 0) {
         tvDescription.setVisibility(View.GONE);
      }
   }

   @OnClick(R.id.ll_more_info)
   public void actionSeeLess() {
      tvDescription.setSingleLine(true);
      tvDescription.setVisibility(View.VISIBLE);
      tvSeeMore.setVisibility(View.VISIBLE);
   }
}
