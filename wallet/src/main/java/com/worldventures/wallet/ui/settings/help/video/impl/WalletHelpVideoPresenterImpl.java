package com.worldventures.wallet.ui.settings.help.video.impl;

import android.net.Uri;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.model.CachedModel;
import com.worldventures.core.modules.video.model.Video;
import com.worldventures.core.modules.video.model.VideoCategory;
import com.worldventures.core.modules.video.model.VideoLanguage;
import com.worldventures.core.modules.video.model.VideoLocale;
import com.worldventures.core.modules.video.service.MemberVideosInteractor;
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand;
import com.worldventures.core.modules.video.service.command.GetVideoLocalesCommand;
import com.worldventures.core.modules.video.service.command.UpdateStatusCachedEntityCommand;
import com.worldventures.core.service.CachedEntityDelegate;
import com.worldventures.core.service.CachedEntityInteractor;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.help.video.WalletHelpVideoPresenter;
import com.worldventures.wallet.ui.settings.help.video.WalletHelpVideoScreen;
import com.worldventures.wallet.ui.settings.help.video.model.WalletVideoModel;

import java.util.Collections;
import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class WalletHelpVideoPresenterImpl extends WalletPresenterImpl<WalletHelpVideoScreen> implements WalletHelpVideoPresenter {

   private final MemberVideosInteractor memberVideosInteractor;
   private final CachedEntityInteractor cachedEntityInteractor;
   private final CachedEntityDelegate cachedEntityDelegate;

   private final WalletHelpVideoDelegate helpVideoDelegate;

   public WalletHelpVideoPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         MemberVideosInteractor memberVideosInteractor,
         CachedEntityInteractor cachedEntityInteractor, CachedEntityDelegate cachedEntityDelegate, WalletHelpVideoDelegate helpVideoDelegate) {
      super(navigator, deviceConnectionDelegate);
      this.memberVideosInteractor = memberVideosInteractor;
      this.cachedEntityInteractor = cachedEntityInteractor;
      this.cachedEntityDelegate = cachedEntityDelegate;
      this.helpVideoDelegate = helpVideoDelegate;
   }

   @Override
   public void attachView(WalletHelpVideoScreen view) {
      super.attachView(view);
      observeUpdateStatusCachedEntity();
      subscribeToCachingStatusUpdates();
      fetchVideoLocales();
   }

   private void observeUpdateStatusCachedEntity() {
      cachedEntityInteractor.updateStatusCachedEntityCommandPipe()
            .observe()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<UpdateStatusCachedEntityCommand>()
                  .onSuccess(command -> handleUpdatedStatusCachedEntities(command.getResult()))
                  .onFail((command, throwable) -> getView().showRefreshing(false))
            );
   }

   private void handleUpdatedStatusCachedEntities(List<VideoCategory> categories) {
      getView().provideVideos(convert(categories.get(0).getVideos()));
      getView().showRefreshing(false);
   }

   private List<WalletVideoModel> convert(List<Video> videos) {
      if (videos.isEmpty()) {
         return Collections.emptyList();
      }
      return Queryable.from(videos).map(WalletVideoModel::new).toList();
   }

   @Override
   public void fetchVideoLocales() {
      memberVideosInteractor.getVideoLocalesPipe()
            .createObservable(new GetVideoLocalesCommand())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationLoadLanguages())
                  .onSuccess(this::handleLoadedLocales)
                  .create());
   }

   private void handleLoadedLocales(GetVideoLocalesCommand command) {
      helpVideoDelegate.setVideoLocales(command.getResult());
      getView().provideVideoLocales(command.getResult());
   }

   @Override
   public void fetchSmartCardVideos(final VideoLanguage videoLanguage) {
      memberVideosInteractor.getMemberVideosPipe()
            .createObservable(GetMemberVideosCommand.forHelpSmartCardVideos(videoLanguage))
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationLoadVideos())
                  .onStart(command -> getView().showRefreshing(true))
                  .onSuccess(command -> onVideoLoaded(command.getResult()))
                  .onFail((command, throwable) -> getView().showRefreshing(false))
                  .create());
   }

   private void onVideoLoaded(List<VideoCategory> categories) {
      cachedEntityInteractor.updateStatusCachedEntityCommandPipe()
            .send(new UpdateStatusCachedEntityCommand(categories));
   }

   @Override
   public void fetchSmartCardVideosForDefaultLocale(final List<VideoLocale> videoLocales) {
      fetchSmartCardVideos(helpVideoDelegate.getDefaultLanguage(videoLocales));
      getView().setSelectedLocale(helpVideoDelegate.getDefaultLocaleIndex(videoLocales));
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void onPlayVideo(WalletVideoModel videoModel) {
      final Uri videoUri = helpVideoDelegate.playVideo(videoModel);
      getNavigator().goVideoPlayer(videoUri, videoModel.getVideo().getVideoName(),
            getClass(), helpVideoDelegate.obtainVideoLanguage(videoModel));
   }

   private void subscribeToCachingStatusUpdates() {
      Observable.merge(
            cachedEntityInteractor.getDownloadCachedModelPipe().observe(),
            cachedEntityInteractor.getDeleteCachedModelPipe().observe())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .map(actionState -> actionState.action.getCachedModel())
            .subscribe(entity -> helpVideoDelegate.processCachingState(entity, getView()));
   }

   @Override
   public void cancelCachingVideo(CachedModel entity) {
      getView().confirmCancelDownload(entity);
   }

   @Override
   public void onCancelAction(CachedModel entity) {
      cachedEntityDelegate.cancelCaching(entity, helpVideoDelegate.getPathForCache(entity));
   }

   @Override
   public void deleteCachedVideo(CachedModel entity) {
      getView().confirmDeleteVideo(entity);
   }

   @Override
   public void onDeleteAction(CachedModel entity) {
      cachedEntityDelegate.deleteCache(entity, helpVideoDelegate.getPathForCache(entity));
   }

   @Override
   public void downloadVideo(CachedModel entity) {
      cachedEntityDelegate.startCaching(entity, helpVideoDelegate.getPathForCache(entity));
   }

   @Override
   public void onSelectedLocale(VideoLocale videoLocale) {
      if (!helpVideoDelegate.isCurrentSelectedVideoLocale(videoLocale)) {
         getView().showDialogChosenLanguage(videoLocale);
      } else {
         helpVideoDelegate.setCurrentSelectedVideoLocale(videoLocale);
      }
   }

   @Override
   public void refreshVideos() {
      fetchSmartCardVideos(helpVideoDelegate.getDefaultLanguageFromLastLocales());
   }

   @Override
   public void onSelectLastLocale() {
      getView().setSelectedLocale(helpVideoDelegate.getLastSelectedLocaleIndex());
   }
}
