package com.messenger.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.messenger.entities.PhotoAttachment;
import com.messenger.ui.module.flagging.FlaggingView;
import com.messenger.ui.module.flagging.FullScreenFlaggingViewImpl;
import com.messenger.ui.presenter.MessageImageFullscreenPresenter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.social.ui.share.ShareType;
import com.worldventures.dreamtrips.social.ui.share.bundle.ShareBundle;
import com.worldventures.dreamtrips.social.ui.flags.view.FlagView;
import com.worldventures.dreamtrips.modules.common.view.dialog.PhotosShareDialog;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.custom.ScaleImageView;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

@Layout(R.layout.fragment_fullscreen_message_photo)
public class MessageImageFullscreenFragment extends BaseFragmentWithArgs<MessageImageFullscreenPresenter, PhotoAttachment>
      implements MessageImageFullscreenPresenter.View {

   @InjectView(R.id.tv_date) TextView attachmentDate;
   @InjectView(R.id.flag) FlagView flagView;
   @InjectView(R.id.iv_image) ScaleImageView imageryView;

   private FlaggingView flaggingView;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      flaggingView = new FullScreenFlaggingViewImpl(rootView, (Injector) getActivity());
   }

   @Override
   public void setContent(PhotoAttachment photo) {
      if (!TextUtils.isEmpty(photo.getUrl())) {
         loadImage(photo.getUrl());
      }
   }

   @Override
   protected MessageImageFullscreenPresenter createPresenter(Bundle savedInstanceState) {
      return new MessageImageFullscreenPresenter(getArgs());
   }

   @OnClick(R.id.flag)
   public void onFlagPressed() {
      getPresenter().onFlagPressed();
   }

   @Override
   public void setDateLabel(String dateLabel) {
      attachmentDate.setText(dateLabel);
   }

   @Override
   public FlaggingView getFlaggingView() {
      return flaggingView;
   }

   @Override
   public void setShowFlag(boolean showFlag) {
      flagView.setVisibility(showFlag ? View.VISIBLE : View.GONE);
   }

   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      flaggingView.onSaveInstanceState(outState);
   }

   @Override
   public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
      super.onViewStateRestored(savedInstanceState);
      flaggingView.onRestoreInstanceState(savedInstanceState);
   }

   private void loadImage(String url) {
      imageryView.requestLayout();

      Runnable task = () -> {
         // this method is called for previous fragment, which has difference between position of
         // this fragment and displayed one is greater than count of visible items divided by 2 + 1
         // And the width of one is 0.
         if (imageryView != null && imageryView.getWidth() > 0 && imageryView.getHeight() > 0) {
            int previewWidth = getResources().getDimensionPixelSize(R.dimen.chat_image_width);
            int previewHeight = getResources().getDimensionPixelSize(R.dimen.chat_image_height);

            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                  .setLowResImageRequest(GraphicUtils.createResizeImageRequest(Uri.parse(url), previewWidth, previewHeight))
                  .setImageRequest(GraphicUtils.createResizeImageRequest(Uri.parse(url), imageryView.getWidth(), imageryView
                        .getHeight()))
                  .build();

            imageryView.setController(draweeController);
         }
      };
      ViewUtils.runTaskAfterMeasure(imageryView, task);
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
      data.setText(text == null ? "" : text);
      data.setShareType(type);
      router.moveTo(Route.SHARE, NavigationConfigBuilder.forActivity().data(data).build());
   }

   @Optional
   @OnClick(R.id.iv_share)
   public void actionShare() {
      getPresenter().onShareAction();
   }

   @Override
   public void onShowShareOptions() {
      new PhotosShareDialog(getActivity(), type -> getPresenter().onShareOptionChosen(type)).show();
   }
}
