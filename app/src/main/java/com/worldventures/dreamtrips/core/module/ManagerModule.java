package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.navigation.service.DialogNavigatorInteractor;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.DTCookieManager;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityDelegate;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.ReplayEventDelegatesWiper;
import com.worldventures.dreamtrips.modules.common.delegate.SocialCropImageManager;
import com.worldventures.dreamtrips.modules.common.delegate.system.AppInfoProvider;
import com.worldventures.dreamtrips.modules.common.delegate.system.AppInfoProviderImpl;
import com.worldventures.dreamtrips.modules.common.delegate.system.ConnectionInfoProvider;
import com.worldventures.dreamtrips.modules.common.delegate.system.ConnectionInfoProviderImpl;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProvider;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProviderImpl;
import com.worldventures.dreamtrips.modules.common.delegate.system.UriPathProvider;
import com.worldventures.dreamtrips.modules.common.delegate.system.UriPathProviderImpl;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.OfflineWarningDelegate;
import com.worldventures.dreamtrips.modules.common.service.ConfigurationInteractor;
import com.worldventures.dreamtrips.modules.common.service.InitializerInteractor;
import com.worldventures.dreamtrips.modules.common.service.OfflineErrorInteractor;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerImagesProcessedEventDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.PhotoPickerDelegate;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegateImpl;
import com.worldventures.dreamtrips.modules.dtl.service.AttributesInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.ClearMemoryInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsFacadeInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsRequestSourceInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.facebook.service.FacebookInteractor;
import com.worldventures.dreamtrips.modules.feed.service.ActiveFeedRouteInteractor;
import com.worldventures.dreamtrips.modules.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.LikesInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.AccountTimelineStorageInteractor;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.FeedStorageInteractor;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.HashtagFeedStorageInteractor;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.UserTimelineStorageInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.DocumentsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.modules.media_picker.service.MediaMetadataInteractor;
import com.worldventures.dreamtrips.modules.profile.service.ProfileInteractor;
import com.worldventures.dreamtrips.modules.reptools.service.SuccessStoriesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.ProgressAnalyticInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.delegate.MemberImagesRefresher;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.EditPhotoTagsCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.PostLocationPickerCallback;
import com.worldventures.dreamtrips.modules.video.service.MemberVideosInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Module(
      injects = {
            PhotoUploadingManagerS3.class,
      },
      library = true, complete = false)
public class ManagerModule {

   @Singleton
   @Provides
   public CirclesInteractor provideQueryCirclesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new CirclesInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   public LikesInteractor provideLikesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new LikesInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   public CommentsInteractor provideCommentsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new CommentsInteractor(sessionActionPipeCreator);
   }

   @Provides
   public PhotoUploadingManagerS3 providePhotoUploadingManagerS3(@ForApplication Injector injector) {
      return new PhotoUploadingManagerS3(injector);
   }

   @Singleton
   @Provides
   ClearMemoryInteractor provideClearMemoryInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new ClearMemoryInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   DtlLocationInteractor provideDtlLocationInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DtlLocationInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   MerchantsInteractor provideMerchantsInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         DtlLocationInteractor locationInteractor, ClearMemoryInteractor clearMemoryInteractor) {
      return new MerchantsInteractor(sessionActionPipeCreator, locationInteractor, clearMemoryInteractor);
   }

   @Singleton
   @Provides
   FullMerchantInteractor provideFullMerchantInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         DtlLocationInteractor dtlLocationInteractor) {
      return new FullMerchantInteractor(sessionActionPipeCreator, dtlLocationInteractor);
   }

   @Singleton
   @Provides
   DtlTransactionInteractor provideDtlTransactionInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DtlTransactionInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   FilterDataInteractor provideFilterDataInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         AnalyticsInteractor analyticsInteractor, DtlLocationInteractor dtlLocationInteractor, MerchantsRequestSourceInteractor merchantsRequestSourceInteractor,
         SnappyRepository snappyRepository) {
      return new FilterDataInteractor(sessionActionPipeCreator, analyticsInteractor, dtlLocationInteractor, merchantsRequestSourceInteractor,
            snappyRepository);
   }

   @Singleton
   @Provides
   MerchantsFacadeInteractor provideMerchantsFacadeInteractor(MerchantsRequestSourceInteractor merchantsRequestSourceInteractor, FilterDataInteractor filterDataInteractor, MerchantsInteractor merchantsInteractor, DtlLocationInteractor locationInteractor) {
      return new MerchantsFacadeInteractor(merchantsRequestSourceInteractor, filterDataInteractor, merchantsInteractor, locationInteractor);
   }

   @Singleton
   @Provides
   AttributesInteractor provideAttributesInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         FilterDataInteractor filterDataInteractor, DtlLocationInteractor dtlLocationInteractor) {
      return new AttributesInteractor(sessionActionPipeCreator, filterDataInteractor, dtlLocationInteractor);
   }

   @Singleton
   @Provides
   MerchantsRequestSourceInteractor provideMerchantsRequestSourceInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new MerchantsRequestSourceInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   PresentationInteractor providePresentationInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new PresentationInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   LocationDelegate provideLocationDelegate(@ForApplication Context context) {
      return new LocationDelegateImpl(context);
   }

   @Provides
   @Singleton
   PhotoPickerDelegate providePhotoPickerDelegate() {
      return new PhotoPickerDelegate();
   }

   @Provides
   @Singleton
   DTCookieManager provideCookieManager(@ForApplication Context context) {
      return new DTCookieManager(context);
   }

   @Provides
   @Singleton
   MediaPickerEventDelegate provideMediaPickerManager() {
      return new MediaPickerEventDelegate();
   }

   @Provides
   @Singleton
   MediaPickerImagesProcessedEventDelegate provideMediaPickerImagesProcessedDelegate(ReplayEventDelegatesWiper wiper) {
      return new MediaPickerImagesProcessedEventDelegate(wiper);
   }

   @Provides
   @Singleton
   SocialCropImageManager provideGlobalConfigManager(@ForApplication Context context) {
      return new SocialCropImageManager(context);
   }

   @Provides
   @Singleton
   EditPhotoTagsCallback provideEditPhotoTagsCallback() {
      return new EditPhotoTagsCallback();
   }

   @Provides
   @Singleton
   PostLocationPickerCallback providePostLocationPickerCallback() {
      return new PostLocationPickerCallback();
   }

   @Provides
   @Singleton
   BucketInteractor provideBucketService(SessionActionPipeCreator sessionActionPipeCreator) {
      return new BucketInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   DownloadFileInteractor provideDownloadFileInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DownloadFileInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   CachedEntityInteractor provideDownloadCachedEntityInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new CachedEntityInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   TripImagesInteractor provideTripImagesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new TripImagesInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   DialogNavigatorInteractor provideDialogNavigatorInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DialogNavigatorInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   SuccessStoriesInteractor provideSuccessStoriesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new SuccessStoriesInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   CachedEntityDelegate provideDownloadFileDelegate(CachedEntityInteractor cachedEntityInteractor) {
      return new CachedEntityDelegate(cachedEntityInteractor);
   }

   @Provides
   @Singleton
   MemberVideosInteractor provideMemberVideosInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new MemberVideosInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   OfflineWarningDelegate provideOfflineWarningDelegate() {
      return new OfflineWarningDelegate();
   }

   @Provides
   @Singleton
   FeedbackInteractor provideFeedbackInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new FeedbackInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   ProfileInteractor provideProfileInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         SessionHolder<UserSession> sessionHolder) {
      return new ProfileInteractor(sessionActionPipeCreator, sessionHolder);
   }

   @Provides
   @Singleton
   ConnectionInfoProvider connectionInfoProvider(Context context) {
      return new ConnectionInfoProviderImpl(context);
   }

   @Provides
   @Singleton
   AppInfoProvider provideAppInfoProvider(Context context) {
      return new AppInfoProviderImpl(context);
   }

   @Provides
   @Singleton
   UriPathProvider provideUriPathProvider(Context context) {
      return new UriPathProviderImpl(context);
   }

   @Provides
   @Singleton
   DeviceInfoProvider provideProfileInteractor(Context context) {
      return new DeviceInfoProviderImpl(context);
   }

   @Provides
   @Singleton
   PostsInteractor providePostsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new PostsInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   OfflineErrorInteractor provideOfflineErrorInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new OfflineErrorInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   DrawableUtil provideDrawableUtil(Context context) {
      return new DrawableUtil(context);
   }

   @Provides
   @Singleton
   DocumentsInteractor provideDocumentsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DocumentsInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   ProgressAnalyticInteractor provideVideoInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new ProgressAnalyticInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   FacebookInteractor provideFacebookInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new FacebookInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   AppConfigurationInteractor provideVersionCheckInteractor(Janet janet) {
      return new AppConfigurationInteractor(janet);
   }

   @Provides
   @Singleton
   FeedStorageInteractor provideFeedStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new FeedStorageInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   AccountTimelineStorageInteractor provideTimelineStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new AccountTimelineStorageInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   UserTimelineStorageInteractor provideUserTimelineStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new UserTimelineStorageInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   HashtagFeedStorageInteractor provideHashtagFeedStorageInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new HashtagFeedStorageInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   InitializerInteractor provideInitializerInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         LoginInteractor loginInteractor) {
      return new InitializerInteractor(sessionActionPipeCreator, loginInteractor);
   }

   @Provides
   @Singleton
   MemberImagesRefresher provideMemberImagesRefresher(TripImagesInteractor tripImagesInteractor) {
      return new MemberImagesRefresher(tripImagesInteractor);
   }

   @Provides
   @Singleton
   UploadingFileManager provideUploadingFileManager(Context context) {
      return new UploadingFileManager(context.getFilesDir());
   }

   @Provides
   @Singleton
   ConfigurationInteractor provideConfigurationInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new ConfigurationInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   MediaMetadataInteractor provideMediaMetadataInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new MediaMetadataInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   ActiveFeedRouteInteractor provideActiveFeedRouteInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new ActiveFeedRouteInteractor(sessionActionPipeCreator);
   }
}
