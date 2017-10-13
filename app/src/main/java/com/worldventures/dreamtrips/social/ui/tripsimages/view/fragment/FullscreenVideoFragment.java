package com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.feed.view.popup.FeedItemMenuBuilder;
import com.worldventures.dreamtrips.social.ui.flags.view.FlagView;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Flag;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.FullscreenVideoPresenter;
import com.worldventures.dreamtrips.social.ui.video.view.custom.VideoView;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

@Layout(R.layout.fragment_fullscreen_video_attchment)
public class FullscreenVideoFragment extends BaseFragmentWithArgs<FullscreenVideoPresenter, Video>
      implements FullscreenVideoPresenter.View {

   @Inject Router router;
   @Inject @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator;

   @InjectView(R.id.videoView) VideoView videoView;
   @InjectView(R.id.flag) FlagView flag;

   private WeakHandler handler = new WeakHandler(Looper.getMainLooper());

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      videoView.setMute(false);
      videoView.setLikeAction(getPresenter()::onLike);
      videoView.setCommentAction(getPresenter()::onComment);
      videoView.setLikesCountAction(getPresenter()::onComment);
      videoView.setCommentsCountAction(getPresenter()::onComment);
      videoView.setEditAction(this::onMore);
      videoView.setFlagAction(() -> getPresenter().onFlag(this));
   }

   private void onMore() {
      FeedItemMenuBuilder.create(getActivity(), videoView.getEditButton(), R.menu.menu_feed_entity_delete)
            .onDelete(this::onDelete)
            .show();
   }

   private void onDelete() {
      Dialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText(getResources().getString(R.string.video_delete))
            .setContentText(getResources().getString(R.string.video_delete_caption))
            .setConfirmText(getResources().getString(R.string.post_delete_confirm))
            .setConfirmClickListener(alertDialog -> {
               alertDialog.dismissWithAnimation();
               getPresenter().onDelete();
            });
      dialog.setCanceledOnTouchOutside(true);
      dialog.show();
   }

   @OnClick(R.id.user_photo)
   void onUserClicked() {
      getPresenter().openUser();
   }

   @Override
   public void openUser(UserBundle bundle) {
      router.moveTo(routeCreator.createRoute(bundle.getUser().getId()), NavigationConfigBuilder.forActivity()
            .data(bundle)
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build());
   }

   @Override
   public void flagSentSuccess() {
      informUser(R.string.flag_sent_success_msg);
   }

   @Override
   public void showFlagProgress() {
      flag.showProgress();
   }

   @Override
   public void hideFlagProgress() {
      flag.hideProgress();
   }

   @Override
   public void showFlagDialog(List<Flag> flags) {
      hideFlagProgress();
      flag.showFlagsPopup(flags, (flagReasonId, reason) -> getPresenter().sendFlagAction(flagReasonId, reason));
   }

   @Override
   public void setVideo(Video video) {
      videoView.setVideo(video);
   }

   @Override
   public void setUserVisibleHint(boolean isVisibleToUser) {
      super.setUserVisibleHint(isVisibleToUser);
      if (isVisibleToUser) {
         if (videoView == null) {
            handler.postDelayed(this::playVideo, 500);
         } else {
            playVideo();
         }
      }
   }

   private void playVideo() {
      videoView.playVideo();
   }

   @Override
   public void setSocialInfo(Video video, boolean enableFlagging, boolean enableDelete) {
      videoView.setSocialInfo(video, enableFlagging, enableDelete);
   }

   @Override
   protected FullscreenVideoPresenter createPresenter(Bundle savedInstanceState) {
      return new FullscreenVideoPresenter(getArgs());
   }
}
