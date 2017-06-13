package com.worldventures.dreamtrips.modules.video.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityDelegate;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.model.MediaHeader;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.model.VideoCategory;
import com.worldventures.dreamtrips.modules.video.service.MemberVideosInteractor;
import com.worldventures.dreamtrips.modules.video.service.command.GetMemberVideosCommand;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class PresentationVideosPresenter<T extends PresentationVideosPresenter.View> extends Presenter<T> {

   @Inject CachedEntityInteractor cachedEntityInteractor;
   @Inject CachedEntityDelegate cachedEntityDelegate;
   @Inject CachedModelHelper cachedModelHelper;
   @Inject protected SnappyRepository db;
   @Inject protected MemberVideosInteractor memberVideosInteractor;

   protected List<Object> currentItems;

   protected GetMemberVideosCommand getMemberVideosRequest() {
      return GetMemberVideosCommand.forMemberVideos();
   }

   @Override
   public void takeView(T view) {
      super.takeView(view);
      memberVideosInteractor.getMemberVideosPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetMemberVideosCommand>()
                  .onStart(getMemberVideosCommand -> view.startLoading())
                  .onSuccess(getMemberVideosCommand -> onVideosLoaded(getMemberVideosCommand.getResult()))
                  .onFail(this::onFail));
   }

   @Override
   public void onResume() {
      super.onResume();
      loadOnStart();
   }

   public void reload() {
      loadVideos();
   }

   protected void loadOnStart() {
      loadVideos();
   }

   protected void loadVideos() {
      memberVideosInteractor.getMemberVideosPipe()
            .send(getMemberVideosRequest());
   }

   protected void onFail(CommandWithError commandWithError, Throwable e) {
      view.finishLoading();
      handleError(commandWithError, e);
   }

   private void onVideosLoaded(List<VideoCategory> categories) {
      view.finishLoading();
      attachCacheToVideos(categories);
      addCategories(categories);
      subscribeToCachingStatusUpdates();
   }

   private void attachCacheToVideos(List<VideoCategory> categories) {
      Queryable.from(categories).forEachR(cat -> Queryable.from(cat.getVideos()).forEachR(video -> {
         CachedModel e = db.getDownloadMediaModel(video.getUid());
         video.setCacheEntity(e);
      }));
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
      List<Video> videos = Queryable.from(currentItems).filter(cat -> (cat instanceof Video))
            .cast(Video.class).toList();
      Queryable.from(videos).notNulls()
            .filter(video -> video.getCacheEntity().getUuid()
                  .equals(cachedModel.getUuid()))
            .forEachR(video -> {
               video.setCacheEntity(cachedModel);
               view.notifyItemChanged(cachedModel);
            });
   }

   protected void addCategories(List<VideoCategory> categories) {
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

   public void downloadVideo(CachedModel entity) {
      cachedEntityDelegate.startCaching(entity, getPathForCache(entity));
   }

   public void deleteCachedVideo(CachedModel entity) {
      view.onDeleteAction(entity);
   }

   public void onDeleteAction(CachedModel entity) {
      cachedEntityDelegate.deleteCache(entity, getPathForCache(entity));
   }

   public void cancelCachingVideo(CachedModel entity) {
      view.onCancelCaching(entity);
   }

   public void onCancelAction(CachedModel entity) {
      cachedEntityDelegate.cancelCaching(entity, getPathForCache(entity));
      TrackingHelper.videoAction(TrackingHelper.ACTION_MEMBERSHIP, getAccountUserId(), TrackingHelper.ACTION_MEMBERSHIP_LOAD_CANCELED, entity
            .getName());
   }

   public void onPlayVideo(Video video) {
      CachedModel videoEntity = video.getCacheEntity();
      Uri parse = Uri.parse(video.getVideoUrl());
      if (cachedModelHelper.isCached(videoEntity)) {
         parse = Uri.parse(cachedModelHelper.getFilePath(videoEntity.getUrl()));
      }

      activityRouter.openPlayerActivity(parse, video.getVideoName(), obtainVideoLanguage(video), getClass());
   }

   protected String obtainVideoLanguage(Video video) {
      return !TextUtils.isEmpty(video.getLanguage())? video.getLanguage() : "null";
   }

   private String getPathForCache(CachedModel entity) {
      return cachedModelHelper.getFilePath(entity.getUrl());
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

      void notifyItemChanged(CachedModel entity);

      void onDeleteAction(CachedModel entity);

      void onCancelCaching(CachedModel entity);
   }
}
