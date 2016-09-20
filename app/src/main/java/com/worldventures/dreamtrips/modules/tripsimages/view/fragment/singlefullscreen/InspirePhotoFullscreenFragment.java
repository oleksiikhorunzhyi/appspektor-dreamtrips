package com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.InspirationFullscreenPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_fullscreen_inspiration_photo)
public class InspirePhotoFullscreenFragment extends FullScreenPhotoFragment<InspirationFullscreenPresenter, Inspiration> implements InspirationFullscreenPresenter.View {

   @InjectView(R.id.tv_description) TextView tvDescription;
   @InjectView(R.id.tv_see_more) TextView tvSeeMore;
   @InjectView(R.id.textViewInspireMeTitle) TextView textViewInspireMeTitle;
   @InjectView(R.id.iv_share) ImageView ivShare;

   @Override
   protected InspirationFullscreenPresenter createPresenter(Bundle savedInstanceState) {
      return new InspirationFullscreenPresenter((Inspiration) getArgs().getPhoto(), getArgs().getType());
   }

   @Override
   public void setContent(IFullScreenObject photo) {
      super.setContent(photo);
      tvDescription.setText(photo.getFSDescription());
      textViewInspireMeTitle.setText("- " + photo.getFSTitle());
   }

   @OnClick(R.id.tv_see_more)
   protected void actionSeeMore() {
      tvDescription.setSingleLine(false);

      tvSeeMore.setVisibility(View.GONE);
      if (tvDescription.getText().length() == 0) {
         tvDescription.setVisibility(View.GONE);
      }
   }

   @OnClick({R.id.actionPanel, R.id.description_container})
   public void actionSeeLess() {
      tvDescription.setSingleLine(true);
      tvDescription.setVisibility(View.VISIBLE);
      tvSeeMore.setVisibility(View.VISIBLE);
   }

   @Override
   public boolean onApiError(ErrorResponse errorResponse) {
      return false;
   }

   @Override
   public void onApiCallFailed() {

   }
}
