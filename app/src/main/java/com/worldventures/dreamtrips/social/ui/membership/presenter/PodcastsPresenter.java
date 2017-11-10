package com.worldventures.dreamtrips.social.ui.membership.presenter;

import android.content.ActivityNotFoundException;
import android.os.Environment;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.model.CachedModel;
import com.worldventures.core.modules.video.utils.CachedModelHelper;
import com.worldventures.core.service.CachedEntityDelegate;
import com.worldventures.core.service.CachedEntityInteractor;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;
import com.worldventures.dreamtrips.social.ui.membership.service.PodcastsInteractor;
import com.worldventures.dreamtrips.social.ui.membership.service.command.GetPodcastsCommand;
import com.worldventures.dreamtrips.social.ui.podcast_player.service.ViewPodcastAnalyticsAction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class PodcastsPresenter<T extends PodcastsPresenter.View> extends Presenter<T> {

   @Inject PodcastsInteractor podcastsInteractor;
   @Inject CachedEntityInteractor cachedEntityInteractor;
   @Inject CachedEntityDelegate cachedEntityDelegate;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject CachedModelHelper cachedModelHelper;

   private boolean loading;
   private boolean hasMore;

   private List<Podcast> podcasts = new ArrayList<>();

   @Override
   public void takeView(T view) {
      super.takeView(view);
      subscribeToApiUpdates();
      subscribeToCachingStatusUpdates();
      loadPodcasts(true);
   }

   public void scrolled(int totalItemCount, int lastVisible) {
      if (!loading && hasMore && lastVisible == totalItemCount - 1) {
         loading = true;
         loadPodcasts(false);
      }
   }

   public void onRefresh() {
      loadPodcasts(true);
   }

   private void loadPodcasts(boolean refresh) {
      loading = true;
      view.startLoading();
      podcastsInteractor.podcastsActionPipe().send(refresh ?
            GetPodcastsCommand.refresh() : GetPodcastsCommand.loadMore());
   }

   private void subscribeToApiUpdates() {
      podcastsInteractor.podcastsActionPipe().observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetPodcastsCommand>()
                  .onProgress((command, progress) -> refreshPodcasts(command.getItems()))
                  .onSuccess(this::onPodcastsFinishedLoading)
                  .onFail(this::onPodcastsLoadingFailed));
   }

   private void onPodcastsFinishedLoading(GetPodcastsCommand successCommand) {
      hasMore = successCommand.hasMore();
      loading = false;
      view.finishLoading();
      refreshPodcasts(successCommand.getItems());
   }

   private void refreshPodcasts(List<Podcast> newPodcasts) {
      podcasts.clear();
      podcasts.addAll(newPodcasts);
      List<Object> items = new ArrayList<>();
      items.add(new MediaHeader(context.getString(R.string.recently_added)));
      items.addAll(podcasts);
      view.setItems(podcasts);
      view.notifyItemChanged(null);
   }

   private void onPodcastsLoadingFailed(GetPodcastsCommand command, Throwable error) {
      super.handleError(command, error);
      view.finishLoading();
      loading = false;
   }

   private void subscribeToCachingStatusUpdates() {
      Observable.merge(cachedEntityInteractor.getDownloadCachedModelPipe().observe(),
            cachedEntityInteractor.getDeleteCachedModelPipe().observe())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .map(actionState -> actionState.action.getCachedModel())
            .subscribe(this::processCachingState);
   }

   private void processCachingState(CachedModel cachedModel) {
      Queryable.from(podcasts).notNulls()
            .filter(podcast -> podcast.getCacheEntity().getUuid()
                  .equals(cachedModel.getUuid()))
            .forEachR(podcast -> {
               podcast.setCacheEntity(cachedModel);
               view.notifyItemChanged(cachedModel);
            });
   }

   public void downloadPodcast(CachedModel entity) {
      cachedEntityDelegate.startCaching(entity, getPathForPodcastCache(entity));
   }

   public void deleteCachedPodcast(CachedModel entity) {
      view.onDeleteAction(entity);
   }

   public void onDeleteAction(CachedModel entity) {
      cachedEntityDelegate.deleteCache(entity, getPathForPodcastCache(entity));
   }

   public void cancelCachingPodcast(CachedModel entity) {
      view.onCancelCaching(entity);
   }

   public void onCancelAction(CachedModel entity) {
      cachedEntityDelegate.cancelCaching(entity, getPathForPodcastCache(entity));
   }

   private String getPathForPodcastCache(CachedModel entity) {
      return cachedModelHelper.getFileForStorage(Environment.DIRECTORY_PODCASTS, entity.getUrl());
   }

   public void play(Podcast podcast) {
      try {
         CachedModel entity = podcast.getCacheEntity();
         if (cachedModelHelper.isCachedPodcast(entity)) {
            String path = cachedModelHelper.getFileForStorage(Environment.DIRECTORY_PODCASTS, podcast.getFileUrl());
            activityRouter.openPodcastPlayer(path, podcast.getTitle());
         } else {
            String url = podcast.getFileUrl();
            activityRouter.openPodcastPlayer(url, podcast.getTitle());
         }
      } catch (ActivityNotFoundException e) {
         view.informUser(R.string.audio_app_not_found_exception);
      }
   }

   public void track() {
      analyticsInteractor.analyticsActionPipe().send(new ViewPodcastAnalyticsAction());
   }

   public interface View extends RxView {

      void startLoading();

      void finishLoading();

      void setItems(List items);

      void notifyItemChanged(CachedModel entity);

      void onDeleteAction(CachedModel entity);

      void onCancelCaching(CachedModel entity);
   }
}