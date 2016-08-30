package com.worldventures.dreamtrips.modules.membership.presenter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.Environment;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.command.CachedEntityCommand;
import com.worldventures.dreamtrips.modules.common.command.DeleteCachedEntityCommand;
import com.worldventures.dreamtrips.modules.common.command.DownloadCachedEntityCommand;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityDelegate;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.membership.command.GetPodcastsCommand;
import com.worldventures.dreamtrips.modules.membership.model.MediaHeader;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;
import com.worldventures.dreamtrips.modules.membership.service.PodcastsInteractor;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class PodcastsPresenter<T extends PodcastsPresenter.View> extends JobPresenter<T> {

   private static final int PODCAST_PRE_PAGE = 10;

   @Inject Context context;
   @Inject ActivityRouter activityRouter;

   @Inject SnappyRepository db;
   @Inject PodcastsInteractor podcastsInteractor;
   @Inject CachedEntityInteractor cachedEntityInteractor;
   @Inject CachedEntityDelegate cachedEntityDelegate;

   private List<Podcast> items = new ArrayList<>();

   private boolean loading;
   private boolean noMoreItems;

   @Override
   public void takeView(T view) {
      super.takeView(view);
      apiErrorPresenter.setView(view);
      subscribeToApiUpdates();
      subscribeToCachingStatusUpdates();
      reloadPodcasts();
   }

   @Override
   public void dropView() {
      apiErrorPresenter.dropView();
      super.dropView();
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

   private void subscribeToApiUpdates() {
      podcastsInteractor.podcastsActionPipe().observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetPodcastsCommand>()
                  .onSuccess(this::onPodcastsLoaded)
                  .onFail(this::onPodcastsLoadingFailed));
   }

   private void onPodcastsLoaded(GetPodcastsCommand command) {
      List<Podcast> podcasts = command.getResult();
      Queryable.from(podcasts).forEachR(podcast
            -> podcast.setCacheEntity(db.getDownloadMediaEntity(podcast.getUid())));
      items.addAll(podcasts);
      updateUi(items);
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

   private void onPodcastsLoadingFailed(GetPodcastsCommand command, Throwable error) {
      apiErrorPresenter.handleActionError(command, error);
      view.finishLoading();
   }

   private void subscribeToCachingStatusUpdates() {
      Observable.merge(cachedEntityInteractor.getDownloadCachedEntityPipe().observe(),
            cachedEntityInteractor.getDeleteCachedEntityPipe().observe())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .map(actionState -> actionState.action.getCachedEntity())
            .subscribe(this::processCachingState);
   }

   private void processCachingState(CachedEntity cachedEntity) {
      Queryable.from(items).notNulls()
            .filter(podcast -> podcast.getCacheEntity().getUuid()
                     .equals(cachedEntity.getUuid()))
            .forEachR(podcast -> {
               podcast.setCacheEntity(cachedEntity);
               view.notifyItemChanged(cachedEntity);
            });
   }

   public void downloadPodcast(CachedEntity entity) {
      cachedEntityDelegate.startCaching(entity, getPathForPodcastCache(entity));
   }

   public void deleteCachedPodcast(CachedEntity entity) {
      view.onDeleteAction(entity);
   }

   public void onDeleteAction(CachedEntity entity) {
      cachedEntityDelegate.deleteCache(entity, getPathForPodcastCache(entity));
   }

   public void cancelCachingPodcast(CachedEntity entity) {
      view.onCancelCaching(entity);
   }

   public void onCancelAction(CachedEntity entity) {
      cachedEntityDelegate.cancelCaching(entity, getPathForPodcastCache(entity));
   }

   private String getPathForPodcastCache(CachedEntity entity) {
      return CachedEntity.getFileForStorage(Environment.DIRECTORY_PODCASTS, entity.getUrl());
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

   public interface View extends RxView, ApiErrorView {

      void startLoading();

      void finishLoading();

      void setItems(List items);

      void notifyItemChanged(CachedEntity entity);

      void onDeleteAction(CachedEntity entity);

      void onCancelCaching(CachedEntity entity);
   }
}
