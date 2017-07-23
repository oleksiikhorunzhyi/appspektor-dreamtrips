package com.worldventures.dreamtrips.wallet.ui.settings.help.video.impl;


import android.net.Uri;

import com.worldventures.dreamtrips.modules.common.command.UpdateStatusCachedEntityCommand;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityDelegate;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.model.VideoCategory;
import com.worldventures.dreamtrips.modules.video.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.video.model.VideoLocale;
import com.worldventures.dreamtrips.modules.video.service.MemberVideosInteractor;
import com.worldventures.dreamtrips.modules.video.service.command.GetMemberVideosCommand;
import com.worldventures.dreamtrips.modules.video.service.command.GetVideoLocalesCommand;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.WalletHelpVideoDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.WalletHelpVideoPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.WalletHelpVideoScreen;

import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.Observable;

public class WalletHelpVideoPresenterImpl extends WalletPresenterImpl<WalletHelpVideoScreen> implements WalletHelpVideoPresenter {
   private final MemberVideosInteractor memberVideosInteractor;
   private final CachedEntityInteractor cachedEntityInteractor;
   private final CachedEntityDelegate cachedEntityDelegate;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;

   private final WalletHelpVideoDelegate helpVideoDelegate;

   public WalletHelpVideoPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, MemberVideosInteractor memberVideosInteractor,
         CachedEntityInteractor cachedEntityInteractor, CachedEntityDelegate cachedEntityDelegate,
         HttpErrorHandlingUtil httpErrorHandlingUtil) {
      super(navigator, smartCardInteractor, networkService);
      this.memberVideosInteractor = memberVideosInteractor;
      this.cachedEntityInteractor = cachedEntityInteractor;
      this.cachedEntityDelegate = cachedEntityDelegate;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
      this.helpVideoDelegate = new WalletHelpVideoDelegate();
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
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<UpdateStatusCachedEntityCommand>()
                  .onSuccess(command -> handleUpdatedStatusCachedEntities(command.getResult()))
                  .onFail((command, throwable) -> getView().showRefreshing(false))
            );
   }

   private void handleUpdatedStatusCachedEntities(List<VideoCategory> categories) {
      getView().provideVideos(categories.get(0).getVideos());
      getView().showRefreshing(false);
   }

   @Override
   public void fetchVideoLocales() {
      memberVideosInteractor.getVideoLocalesPipe()
            .createObservable(new GetVideoLocalesCommand())
            .compose(bindViewIoToMainComposer())
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
            .compose(bindViewIoToMainComposer())
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
   public void onPlayVideo(Video video) {
      final Uri videoUri = helpVideoDelegate.playVideo(getView().getViewContext(), video);
      getNavigator().goVideoPlayer(getView().getViewContext(), videoUri, video.getVideoName(),
            getClass(), helpVideoDelegate.obtainVideoLanguage(video));
   }

   private void subscribeToCachingStatusUpdates() {
      Observable.merge(
            cachedEntityInteractor.getDownloadCachedModelPipe().observe(),
            cachedEntityInteractor.getDeleteCachedModelPipe().observe())
            .compose(bindViewIoToMainComposer())
            .map(actionState -> actionState.action.getCachedModel())
            .subscribe(entity -> helpVideoDelegate.processCachingState(entity, getView()));
   }

   @Override
   public void cancelCachingVideo(CachedModel entity) {
      getView().confirmCancelDownload(entity);
   }

   @Override
   public void onCancelAction(CachedModel entity) {
      cachedEntityDelegate.cancelCaching(entity, helpVideoDelegate.getPathForCache(getView().getViewContext(), entity));
   }

   @Override
   public void deleteCachedVideo(CachedModel entity) {
      getView().confirmDeleteVideo(entity);
   }

   @Override
   public void onDeleteAction(CachedModel entity) {
      cachedEntityDelegate.deleteCache(entity, helpVideoDelegate.getPathForCache(getView().getViewContext(), entity));
   }

   @Override
   public void downloadVideo(CachedModel entity) {
      cachedEntityDelegate.startCaching(entity, helpVideoDelegate.getPathForCache(getView().getViewContext(), entity));
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

   @Override
   public HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }
}
