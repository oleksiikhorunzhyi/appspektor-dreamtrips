package com.worldventures.dreamtrips.wallet.ui.settings.help.video;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
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
import com.worldventures.dreamtrips.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.Observable;

public class WalletHelpVideoPresenter extends WalletPresenter<WalletHelpVideoPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject MemberVideosInteractor memberVideosInteractor;
   @Inject CachedEntityInteractor cachedEntityInteractor;
   @Inject CachedEntityDelegate cachedEntityDelegate;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   private final WalletHelpVideoDelegate helpVideoDelegate;

   public WalletHelpVideoPresenter(Context context, Injector injector) {
      super(context, injector);
      helpVideoDelegate = new WalletHelpVideoDelegate(context);
   }

   @Override
   public void attachView(Screen view) {
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

   void fetchVideoLocales() {
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

   void fetchSmartCardVideos(final VideoLanguage videoLanguage) {
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

   void fetchSmartCardVideosForDefaultLocale(final List<VideoLocale> videoLocales) {
      fetchSmartCardVideos(helpVideoDelegate.getDefaultLanguage(videoLocales));
      getView().setSelectedLocale(helpVideoDelegate.getDefaultLocaleIndex(videoLocales));
   }

   void goBack() {
      navigator.goBack();
   }

   void onPlayVideo(Video video) {
      helpVideoDelegate.playVideo(video);
   }

   private void subscribeToCachingStatusUpdates() {
      Observable.merge(
            cachedEntityInteractor.getDownloadCachedModelPipe().observe(),
            cachedEntityInteractor.getDeleteCachedModelPipe().observe())
            .compose(bindViewIoToMainComposer())
            .map(actionState -> actionState.action.getCachedModel())
            .subscribe(entity -> helpVideoDelegate.processCachingState(entity, getView()));
   }

   void cancelCachingVideo(CachedModel entity) {
      getView().confirmCancelDownload(entity);
   }

   void onCancelAction(CachedModel entity) {
      cachedEntityDelegate.cancelCaching(entity, helpVideoDelegate.getPathForCache(entity));
   }

   void deleteCachedVideo(CachedModel entity) {
      getView().confirmDeleteVideo(entity);
   }

   void onDeleteAction(CachedModel entity) {
      cachedEntityDelegate.deleteCache(entity, helpVideoDelegate.getPathForCache(entity));
   }

   void downloadVideo(CachedModel entity) {
      cachedEntityDelegate.startCaching(entity, helpVideoDelegate.getPathForCache(entity));
   }

   void onSelectedLocale(VideoLocale videoLocale) {
      if (!helpVideoDelegate.isCurrentSelectedVideoLocale(videoLocale)) {
         getView().showDialogChosenLanguage(videoLocale);
      } else {
         helpVideoDelegate.setCurrentSelectedVideoLocale(videoLocale);
      }
   }

   void refreshVideos() {
      fetchSmartCardVideos(helpVideoDelegate.getDefaultLanguageFromLastLocales());
   }

   void onSelectLastLocale() {
      getView().setSelectedLocale(helpVideoDelegate.getLastSelectedLocaleIndex());
   }

   HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }

   public interface Screen extends HelpScreen {

      void showRefreshing(boolean show);
   }
}
