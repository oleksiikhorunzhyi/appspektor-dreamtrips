package com.worldventures.dreamtrips.modules.tripsimages.view.fragment.inspire_me;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.PhotosShareDialog;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.inspire_me.FullscreenInspireMePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.ImageryView;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_fullscreen_inspiration_photo)
public class FullscreenInspireMeFragment extends BaseFragmentWithArgs<FullscreenInspireMePresenter, Inspiration>
      implements FullscreenInspireMePresenter.View {

   @InjectView(R.id.tv_description) TextView description;
   @InjectView(R.id.tv_see_more) TextView seeMore;
   @InjectView(R.id.title) TextView title;
   @InjectView(R.id.iv_image) ImageryView imageryView;

   @Override
   protected FullscreenInspireMePresenter createPresenter(Bundle savedInstanceState) {
      return new FullscreenInspireMePresenter(getArgs());
   }

   @Override
   public void setPhoto(Inspiration photo) {
      imageryView.loadImage(photo.getUrl());
      imageryView.setOnErrorAction(getPresenter()::handleError);
      title.setText(String.format("- %s", photo.getAuthor()));
      description.setText(String.format("\"%s\"", photo.getQuote()));
   }

   @OnClick(R.id.tv_see_more)
   protected void actionSeeMore() {
      description.setSingleLine(false);

      seeMore.setVisibility(View.GONE);
      if (description.getText().length() == 0) {
         description.setVisibility(View.GONE);
      }
   }

   @OnClick({R.id.actionPanel, R.id.description_container})
   public void actionSeeLess() {
      description.setSingleLine(true);
      description.setVisibility(View.VISIBLE);
      seeMore.setVisibility(View.VISIBLE);
   }

   @Override
   public void onDestroyView() {
      if (imageryView != null && imageryView.getController() != null) imageryView.getController().onDetach();
      super.onDestroyView();
   }

   @Override
   public void openShare(String imageUrl, String text, @ShareType String type) {
      ShareBundle data = new ShareBundle();
      data.setImageUrl(imageUrl);
      data.setText(text);
      data.setShareType(type);
      router.moveTo(Route.SHARE, NavigationConfigBuilder.forActivity().data(data).build());
   }

   @OnClick(R.id.iv_share)
   public void actionShare() {
      getPresenter().onShareAction();
   }

   @Override
   public void onShowShareOptions() {
      new PhotosShareDialog(getActivity(), type -> getPresenter().onShareOptionChosen(type)).show();
   }
}
