package com.worldventures.dreamtrips.modules.video.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityDelegate;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.model.MediaHeader;
import com.worldventures.dreamtrips.modules.video.api.MemberVideosRequest;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Category;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class PresentationVideosPresenter<T extends PresentationVideosPresenter.View> extends Presenter<T> {

   @Inject protected SnappyRepository db;
   @Inject CachedEntityInteractor cachedEntityInteractor;
   @Inject CachedEntityDelegate cachedEntityDelegate;

   protected List<Object> currentItems;

   protected MemberVideosRequest getMemberVideosRequest() {
      return new MemberVideosRequest(DreamTripsApi.TYPE_MEMBER);
   }

   @Override
   public void onResume() {
      super.onResume();
      view.startLoading();
      loadOnStart();
   }

   public void reload() {
      loadVideos();
   }

   protected void loadOnStart() {
      loadVideos();
   }

   protected void loadVideos() {
      doRequest(getMemberVideosRequest(), categories -> {
         view.finishLoading();
         attachCacheToVideos(categories);
         addCategories(categories);
         subscribeToCachingStatusUpdates();
      }, spiceException -> view.finishLoading());
   }

   private void attachCacheToVideos(List<Category> categories) {
      Queryable.from(categories).forEachR(cat -> Queryable.from(cat.getVideos()).forEachR(video -> {
         CachedEntity e = db.getDownloadMediaEntity(video.getUid());
         video.setCacheEntity(e);
      }));
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
      List<Video> videos = Queryable.from(currentItems).filter(cat -> (cat instanceof Video))
            .cast(Video.class).toList();
      Queryable.from(videos).notNulls()
            .filter(video -> video.getCacheEntity().getUuid()
                  .equals(cachedEntity.getUuid()))
            .forEachR(video -> {
               video.setCacheEntity(cachedEntity);
               view.notifyItemChanged(cachedEntity);
            });
   }

   protected void addCategories(List<Category> categories) {
      currentItems = new ArrayList<>();
      Queryable.from(categories)
            .forEachR(category -> addCategoryHeader(category.getCategory(), category.getVideos(), categories.indexOf(category)));
      view.setItems(currentItems);
   }

   protected void addCategoryHeader(String category, List<Video> videos, int categoryIndex) {
      currentItems.add(new MediaHeader(category));
      currentItems.addAll(videos);
   }

   public void sendAnalytic(String action, String name) {
      TrackingHelper.actionMembershipVideo(action, name);
   }

   public void downloadVideo(CachedEntity entity) {
      cachedEntityDelegate.startCaching(entity, getPathForCache(entity));
   }

   public void deleteCachedVideo(CachedEntity entity) {
      view.onDeleteAction(entity);
   }

   public void onDeleteAction(CachedEntity entity) {
      cachedEntityDelegate.deleteCache(entity, getPathForCache(entity));
   }

   public void cancelCachingVideo(CachedEntity entity) {
      view.onCancelCaching(entity);
   }

   public void onCancelAction(CachedEntity entity) {
      cachedEntityDelegate.cancelCaching(entity, getPathForCache(entity));
      TrackingHelper.videoAction(TrackingHelper.ACTION_MEMBERSHIP, getAccountUserId(), TrackingHelper.ACTION_MEMBERSHIP_LOAD_CANCELED, entity
            .getName());
   }

   public String getPathForCache(CachedEntity entity) {
      return CachedEntity.getFilePath(context, entity.getUrl());
   }

   public void track() {
      if (isNeedToSendAnalytics()) TrackingHelper.memberVideos(getAccountUserId());
   }

   protected boolean isNeedToSendAnalytics() {
      return true;
   }

   public interface View extends Presenter.View {

      void startLoading();

      void finishLoading();

      void setItems(List<Object> videos);

      void notifyItemChanged(CachedEntity entity);

      void onDeleteAction(CachedEntity entity);

      void onCancelCaching(CachedEntity entity);
   }
}
