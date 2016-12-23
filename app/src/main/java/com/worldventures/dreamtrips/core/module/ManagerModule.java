package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.navigation.service.DialogNavigatorInteractor;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.DTCookieManager;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityDelegate;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.ReplayEventDelegatesWiper;
import com.worldventures.dreamtrips.modules.common.delegate.SocialCropImageManager;
import com.worldventures.dreamtrips.modules.common.delegate.system.ConnectionInfoProvider;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProvider;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProviderImpl;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.OfflineWarningDelegate;
import com.worldventures.dreamtrips.modules.common.service.OfflineErrorInteractor;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerImagesProcessedEventDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.PhotoPickerDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegateImpl;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.LikesInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.DocumentsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.modules.profile.service.ProfileInteractor;
import com.worldventures.dreamtrips.modules.reptools.service.SuccessStoriesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.EditPhotoTagsCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.PostLocationPickerCallback;
import com.worldventures.dreamtrips.modules.video.service.MemberVideosInteractor;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            PhotoUploadingManagerS3.class,
            DtlFilterMerchantInteractor.class,
            DtlMerchantInteractor.class,
            DtlTransactionInteractor.class,
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
   DtlMerchantInteractor dtlMerchantInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         DtlLocationInteractor locationInteractor) {
      return new DtlMerchantInteractor(sessionActionPipeCreator, locationInteractor);
   }

   @Singleton
   @Provides
   DtlFilterMerchantInteractor dtlFilteredMerchantInteractor(DtlMerchantInteractor dtlMerchantInteractor,
         DtlLocationInteractor locationInteractor, LocationDelegate locationDelegate, SessionActionPipeCreator sessionActionPipeCreator) {
      return new DtlFilterMerchantInteractor(dtlMerchantInteractor, locationInteractor, locationDelegate, sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   DtlTransactionInteractor provideDtlTransactionInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         @Named(JanetModule.JANET_API_LIB) SessionActionPipeCreator sessionApiActionPipeCreator) {
      return new DtlTransactionInteractor(sessionActionPipeCreator, sessionApiActionPipeCreator);
   }

   @Singleton
   @Provides
   DtlLocationInteractor provideDtlLocationService(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DtlLocationInteractor(sessionActionPipeCreator);
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
      return new ConnectionInfoProvider(context);
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
}
