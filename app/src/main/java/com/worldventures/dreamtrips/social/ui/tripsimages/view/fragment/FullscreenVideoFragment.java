package com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.FragmentClassProviderModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.feed.view.popup.FeedItemMenuBuilder;
import com.worldventures.dreamtrips.social.ui.flags.view.FlagView;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Flag;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.SocialViewPagerState;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.FullscreenVideoPresenter;
import com.worldventures.dreamtrips.social.ui.video.view.custom.DTVideoConfig;
import com.worldventures.dreamtrips.social.ui.video.view.custom.DTVideoViewImpl;
import com.worldventures.dreamtrips.social.ui.video.view.custom.VideoPlayerHolder;

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
   @Inject VideoPlayerHolder videoPlayerHolder;
   @Inject @ForActivity Injector injector;
   @Inject @Named(FragmentClassProviderModule.PROFILE) FragmentClassProvider<Integer> fragmentClassProvider;
   @Inject SocialViewPagerState socialViewPagerState;

   @InjectView(R.id.videoView) DTVideoViewImpl dtVideoView;
   @InjectView(R.id.flag) FlagView flag;

   private WeakHandler handler = new WeakHandler(Looper.getMainLooper());

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      dtVideoView.setLikeAction(getPresenter()::onLike);
      dtVideoView.setCommentAction(getPresenter()::onComment);
      dtVideoView.setLikesCountAction(getPresenter()::onComment);
      dtVideoView.setCommentsCountAction(getPresenter()::onComment);
      dtVideoView.setEditAction(this::onMore);
      dtVideoView.setFlagAction(() -> getPresenter().onFlag(this));
      dtVideoView.hideFullscreenButton();
      dtVideoView.setThumbnailAction(() -> getPresenter().playVideoRequired());
   }

   private void onMore() {
      FeedItemMenuBuilder.create(getActivity(), dtVideoView.getEditButton(), R.menu.menu_feed_entity_delete)
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
      router.moveTo(fragmentClassProvider.provideFragmentClass(bundle.getUser()
            .getId()), NavigationConfigBuilder.forActivity()
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
   public void setVideoThumbnail(String thumbnail) {
      dtVideoView.setThumbnail(thumbnail);
   }

   @Override
   public void playVideo(Video video) {
      if (videoPlayerHolder.getCurrentVideoConfig() != null
            && video.getUid().equals(videoPlayerHolder.getCurrentVideoConfig().getUid())) {
         videoPlayerHolder.reattachVideoView(dtVideoView, false);
      } else {
         dtVideoView.playVideo(new DTVideoConfig(video.getUid(), false,
               video.getQualities(), 0));
      }
   }

   @Override
   public void setUserVisibleHint(boolean isVisibleToUser) {
      super.setUserVisibleHint(isVisibleToUser);
      if (isVisibleToUser) {
         if (dtVideoView == null) {
            handler.postDelayed(() -> getPresenter().playVideoRequired(), 500);
         } else {
            getPresenter().playVideoRequired();
         }
      }
   }

   @Override
   public void onPause() {
      super.onPause();
      dtVideoView.pauseVideo();
   }

   @Override
   public void setSocialInfo(Video video, boolean enableFlagging, boolean enableDelete) {
      dtVideoView.setSocialInfo(video, enableFlagging, enableDelete);
   }

   @Override
   protected FullscreenVideoPresenter createPresenter(Bundle savedInstanceState) {
      return new FullscreenVideoPresenter(getArgs());
   }
}
