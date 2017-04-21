package com.worldventures.dreamtrips.wallet.ui.settings.help.video;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.command.UpdateStatusCachedEntityCommand;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityDelegate;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.model.VideoCategory;
import com.worldventures.dreamtrips.modules.video.model.VideoLanguage;
import com.worldventures.dreamtrips.modules.video.model.VideoLocale;
import com.worldventures.dreamtrips.modules.video.service.MemberVideosInteractor;
import com.worldventures.dreamtrips.modules.video.service.command.GetMemberVideosCommand;
import com.worldventures.dreamtrips.modules.video.service.command.GetVideoLocalesCommand;
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
                  .onSuccess(command -> getView().provideVideos(command.getResult().get(0).getVideos()))
            );
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
                  .onSuccess(command -> onVideoLoaded(command.getResult()))
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
            cachedEntityInteractor.getDownloadCachedEntityPipe().observe(),
            cachedEntityInteractor.getDeleteCachedEntityPipe().observe())
            .compose(bindViewIoToMainComposer())
            .map(actionState -> actionState.action.getCachedEntity())
            .subscribe(entity -> helpVideoDelegate.processCachingState(entity, getView()));
   }

   void cancelCachingVideo(CachedEntity entity) {
      getView().confirmCancelDownload(entity);
   }

   void onCancelAction(CachedEntity entity) {
      cachedEntityDelegate.cancelCaching(entity, helpVideoDelegate.getPathForCache(entity));
   }

   void deleteCachedVideo(CachedEntity entity) {
      getView().confirmDeleteVideo(entity);
   }

   void onDeleteAction(CachedEntity entity) {
      cachedEntityDelegate.deleteCache(entity, helpVideoDelegate.getPathForCache(entity));
   }

   void downloadVideo(CachedEntity entity) {
      cachedEntityDelegate.startCaching(entity, helpVideoDelegate.getPathForCache(entity));
   }

   void onSelectedLocale(VideoLocale videoLocale) {
      if (!helpVideoDelegate.isCurrentSelectedVideoLocale(videoLocale)) {
         getView().showDialogChosenLanguage(videoLocale);
      } else {
         helpVideoDelegate.setCurrentSelectedVideoLocale(videoLocale);
      }
   }

   void onSelectLastLocale() {
      getView().setSelectedLocale(helpVideoDelegate.getLastSelectedLocaleIndex());
   }

   public interface Screen extends HelpScreen {

   }
}
