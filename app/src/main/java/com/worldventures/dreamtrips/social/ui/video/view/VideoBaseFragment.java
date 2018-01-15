package com.worldventures.dreamtrips.social.ui.video.view;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.core.model.CachedModel;
import com.worldventures.core.modules.video.model.Video;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.social.ui.video.cell.delegate.VideoCellDelegate;
import com.worldventures.dreamtrips.social.ui.video.presenter.VideoBasePresenter;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import butterknife.InjectView;

import static com.worldventures.dreamtrips.social.ui.video.view.util.VideoViewFunctionsKt.showDialog;

public abstract class VideoBaseFragment<T extends VideoBasePresenter> extends BaseFragment<T> implements VideoBasePresenter.View,
      SwipeRefreshLayout.OnRefreshListener, VideoCellDelegate {

   @Inject ActivityRouter activityRouter;

   @InjectView(R.id.swipe_container) protected SwipeRefreshLayout refreshLayout;

   private WeakHandler weakHandler;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      weakHandler = new WeakHandler();
   }

   @Override
   protected abstract T createPresenter(Bundle savedInstanceState);

   @Override
   public void onRefresh() {
      getPresenter().reload();
   }

   @Override
   public void onDeleteAction(@NotNull CachedModel cacheEntity) {
      showDialog(getContext(), R.string.delete_cached_video_title, R.string.delete_cached_video_text, R.string.delete_photo_positiove, R.string.delete_photo_negative,
            () -> getPresenter().deleteAccepted(cacheEntity));
   }

   @Override
   public void onCancelCaching(@NotNull CachedModel cacheEntity) {
      showDialog(getContext(), R.string.cancel_cached_video_title, R.string.cancel_cached_video_text, R.string.cancel_photo_positiove, R.string.cancel_photo_negative,
            () -> getPresenter().cancelCachingAccepted(cacheEntity));
   }

   @Override
   public void startLoading() {
      weakHandler.post(() -> {
         if (refreshLayout != null) {
            refreshLayout.setRefreshing(true);
         }
      });
   }

   @Override
   public void finishLoading() {
      weakHandler.post(() -> {
         if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
         }
      });
   }

   @Override
   public void openPlayer(@NotNull Uri uri, @NotNull String videoName, @NotNull String language) {
      activityRouter.openPlayerActivity(uri, videoName, language, this.getClass());
   }

   @Override
   public void onDownloadMedia(Video video) {
      getPresenter().downloadVideoRequired(video);
   }

   @Override
   public void onDeleteMedia(Video video) {
      getPresenter().deleteCacheRequired(video);
   }

   @Override
   public void onCancelCachingMedia(Video video) {
      getPresenter().cancelCachingRequired(video);
   }

   @Override
   public void onPlayVideoClicked(@NotNull Video entity) {
      getPresenter().onPlayVideo(entity);
   }

   @Override
   public void onCellClicked(Video model) {}
}
