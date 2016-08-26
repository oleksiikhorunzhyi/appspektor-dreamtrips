package com.worldventures.dreamtrips.modules.membership.presenter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.Environment;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.FileDownloadSpiceManager;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.membership.command.GetPodcastsCommand;
import com.worldventures.dreamtrips.modules.membership.model.MediaHeader;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;
import com.worldventures.dreamtrips.modules.membership.service.PodcastsInteractor;
import com.worldventures.dreamtrips.modules.video.FileCachingDelegate;
import com.worldventures.dreamtrips.modules.video.PublicMediaCachingDelegate;
import com.worldventures.dreamtrips.modules.video.api.DownloadFileListener;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;

public class PodcastsPresenter<T extends PodcastsPresenter.View> extends JobPresenter<T> {

   @Inject protected SnappyRepository db;
   @Inject @ForApplication protected Injector injector;
   @Inject protected FileDownloadSpiceManager fileDownloadSpiceManager;
   @Inject protected Janet janet;
   @Inject protected Context context;
   @Inject protected ActivityRouter activityRouter;
   @Inject PodcastsInteractor podcastsInteractor;

   private static final int PODCAST_PRE_PAGE = 10;

   private PublicMediaCachingDelegate fileCachingDelegate;

   private List<Podcast> items = new ArrayList<>();

   private boolean loading;
   private boolean noMoreItems;

   @Override
   public void takeView(T view) {
      super.takeView(view);
      apiErrorPresenter.setView(view);
      fileCachingDelegate = new PublicMediaCachingDelegate(db, context, injector, fileDownloadSpiceManager, Environment.DIRECTORY_PODCASTS);
      fileCachingDelegate.setView(this.view);
      subscribeGetPodcasts();
      reloadPodcasts();
   }

   @Override
   public void dropView() {
      apiErrorPresenter.dropView();
      super.dropView();
   }

   @Override
   public void onStart() {
      super.onStart();
      if (!fileDownloadSpiceManager.isStarted()) {
         fileDownloadSpiceManager.start(context);
      }
   }

   @Override
   public void onStop() {
      super.onStop();
      if (fileDownloadSpiceManager.isStarted()) {
         fileDownloadSpiceManager.shouldStop();
      }
   }

   public void onDeleteAction(CachedEntity entity) {
      fileCachingDelegate.onDeleteAction(entity);
   }

   public void onCancelAction(CachedEntity entity) {
      fileCachingDelegate.onCancelAction(entity);
   }

   public void scrolled(int totalItemCount, int lastVisible) {
      if (!loading && !noMoreItems && lastVisible == totalItemCount - 1) {
         loading = true;
         loadMore();
      }
   }

   protected void loadMore() {
      if (items.size() > 0) {
         loadPodcasts(items.size() / PODCAST_PRE_PAGE + 1);
      }
   }

   public void reloadPodcasts() {
      items.clear();
      loadPodcasts(1);
   }

   private void loadPodcasts(int page) {
      loading = true;
      view.startLoading();
      podcastsInteractor.podcastsActionPipe().send(new GetPodcastsCommand(page, PODCAST_PRE_PAGE));
   }

   private void subscribeGetPodcasts() {
      podcastsInteractor.podcastsActionPipe().observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetPodcastsCommand>()
                  .onSuccess(this::onPodcastsLoaded)
                  .onFail(this::onPodcastsLoadingFailed));
   }

   private void onPodcastsLoaded(GetPodcastsCommand command) {
      List<Podcast> podcasts = command.getResult();
      Queryable.from(podcasts).forEachR(podcast -> {
         attachCache(podcast);
         attachPodcastDownloadListener(podcast);
      });
      items.addAll(podcasts);
      updateUi(items);
   }

   private void onPodcastsLoadingFailed(GetPodcastsCommand command, Throwable error) {
      apiErrorPresenter.handleActionError(command, error);
      view.finishLoading();
   }

   private void attachCache(Podcast podcast) {
      CachedEntity e = db.getDownloadMediaEntity(podcast.getUid());
      podcast.setCacheEntity(e);
   }

   protected void updateUi(List<Podcast> podcasts) {
      List<Object> items = new ArrayList<>();
      items.add(new MediaHeader(context.getString(R.string.recently_added)));
      items.addAll(podcasts);
      view.setItems(items);
      view.notifyItemChanged(null);
      view.finishLoading();

      noMoreItems = podcasts.size() < PODCAST_PRE_PAGE;
      loading = false;
   }

   private void attachPodcastDownloadListener(Podcast podcast) {
      CachedEntity entity = podcast.getCacheEntity();
      boolean failed = entity.isFailed();
      boolean inProgress = entity.getProgress() > 0;
      boolean cached = entity.isCached(Environment.DIRECTORY_PODCASTS);
      if (!failed && inProgress && !cached) {
         DownloadFileListener listener = new DownloadFileListener(entity, fileCachingDelegate);
         injector.inject(listener);
         fileDownloadSpiceManager.addListenerIfPending(InputStream.class, entity.getUuid(), listener);
      }
   }

   public void downloadPodcast(CachedEntity entity) {
      fileCachingDelegate.downloadFile(entity);
   }

   public void deleteCachedPodcast(CachedEntity entity) {
      fileCachingDelegate.deleteCachedFile(entity);
   }

   public void cancelCachingPodcast(CachedEntity entity) {
      fileCachingDelegate.cancelCachingFile(entity);
   }

   public void play(Podcast podcast) {
      try {
         CachedEntity entity = podcast.getCacheEntity();
         if (entity.isCached(Environment.DIRECTORY_PODCASTS)) {
            String path = CachedEntity.getFileForStorage(Environment.DIRECTORY_PODCASTS, podcast.getFileUrl());
            activityRouter.openPodcastPlayer(path);
         } else {
            String url = podcast.getFileUrl();
            activityRouter.openPodcastPlayer(url);
         }
      } catch (ActivityNotFoundException e) {
         view.informUser(R.string.audio_app_not_found_exception);
      }
   }

   public void track() {
      TrackingHelper.podcasts(getAccountUserId());
   }

   public interface View extends RxView, FileCachingDelegate.View, ApiErrorView {

      void startLoading();

      void finishLoading();

      void setItems(List items);
   }
}
