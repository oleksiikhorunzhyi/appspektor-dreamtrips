package com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.ysbh;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldventures.core.model.ShareType;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.dialog.PhotosShareDialog;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.social.ui.share.bundle.ShareBundle;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh.FullscreenYsbhPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.custom.ImageryView;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_fullscreen_ysbh_photo)
public class FullscreenYsbhFragment extends BaseFragmentWithArgs<FullscreenYsbhPresenter, YSBHPhoto>
      implements FullscreenYsbhPresenter.View {

   @InjectView(R.id.tv_description) TextView description;
   @InjectView(R.id.tv_see_more) TextView seeMore;
   @InjectView(R.id.iv_image) ImageryView imageryView;
   @InjectView(R.id.ll_more_info) LinearLayout moreInfo;

   @Override
   protected FullscreenYsbhPresenter createPresenter(Bundle savedInstanceState) {
      return new FullscreenYsbhPresenter(getArgs());
   }

   @Override
   public void setPhoto(YSBHPhoto photo) {
      imageryView.loadImage(photo.getUrl());
      imageryView.setOnErrorAction(getPresenter()::handleError);
      description.setText(photo.getTitle());
   }

   @OnClick(R.id.tv_see_more)
   protected void actionSeeMore() {
      moreInfo.setVisibility(View.VISIBLE);
      description.setSingleLine(false);

      seeMore.setVisibility(View.GONE);
      if (description.getText().length() == 0) {
         description.setVisibility(View.GONE);
      }
   }

   @OnClick(R.id.ll_more_info)
   public void actionSeeLess() {
      description.setSingleLine(true);
      description.setVisibility(View.VISIBLE);
      seeMore.setVisibility(View.VISIBLE);
   }

   @Override
   public void onDestroyView() {
      if (imageryView != null && imageryView.getController() != null) {
         imageryView.getController().onDetach();
      }
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
