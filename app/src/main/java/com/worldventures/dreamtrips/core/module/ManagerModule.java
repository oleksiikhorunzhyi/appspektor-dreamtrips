package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.google.gson.Gson;
import com.messenger.storage.dao.PhotoDAO;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.DreamSpiceService;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.api.SocialUploaderyManager;
import com.worldventures.dreamtrips.core.api.VideoDownloadSpiceManager;
import com.worldventures.dreamtrips.core.api.VideoDownloadSpiceService;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.AuthorizedDataUpdater;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.DTCookieManager;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.common.delegate.GlobalConfigManager;
import com.worldventures.dreamtrips.modules.common.delegate.SocialCropImageManager;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.ClearDirectoryDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.LogoutDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.common.view.util.PhotoPickerDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.store.DtlJobManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.membership.api.PhoneContactRequest;
import com.worldventures.dreamtrips.modules.trips.manager.TripFilterDataProvider;
import com.worldventures.dreamtrips.modules.trips.manager.TripMapManager;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.EditPhotoTagsCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.PostLocationPickerCallback;
import com.worldventures.dreamtrips.modules.video.VideoCachingDelegate;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import io.techery.janet.Janet;

@Module(
        injects = {
                DreamSpiceManager.class,
                DreamSpiceService.class,
                AuthorizedDataUpdater.class,
                VideoCachingDelegate.class,
                VideoDownloadSpiceService.class,
                PhotoUploadingManagerS3.class,
                BucketItemManager.class,
                SocialUploaderyManager.class,
                //
                DownloadVideoListener.class,
                PhoneContactRequest.class,

                LogoutDelegate.class,
                //
                DtlLocationManager.class,
                DtlMerchantManager.class,
                DtlJobManager.class,

                GlobalConfigManager.class,
        },
        library = true, complete = false
)
public class ManagerModule {

    @Provides
    public DreamSpiceManager provideSpiceManager(@ForApplication Injector injector) {
        return new DreamSpiceManager(DreamSpiceService.class, injector);
    }

    @Provides
    public AuthorizedDataUpdater provideDataUpdater(@ForApplication Injector injector) {
        return new AuthorizedDataUpdater(injector);
    }

    @Provides
    @Singleton
    public SocialUploaderyManager provideSocialUploaderyManager(@ForApplication Injector injector) {
        return new SocialUploaderyManager(injector);
    }

    @Provides
    public PhotoUploadingManagerS3 providePhotoUploadingManagerS3(@ForApplication Injector injector) {
        return new PhotoUploadingManagerS3(injector);
    }

    @Provides
    public VideoDownloadSpiceManager provideVideoDownloadSpiceManager(@ForApplication Injector injector) {
        return new VideoDownloadSpiceManager(VideoDownloadSpiceService.class);
    }

    @Singleton
    @Provides
    public BucketItemManager provideBucketItemManager(@ForApplication Injector injector) {
        return new BucketItemManager(injector);
    }

    @Singleton
    @Provides
    DtlLocationManager dtlLocationStore(@ForApplication Injector injector) {
        return new DtlLocationManager(injector);
    }

    @Singleton
    @Provides
    DtlMerchantManager dtlMerchantDelegate(@ForApplication Injector injector) {
        return new DtlMerchantManager(injector);
    }

    @Singleton
    @Provides
    DtlJobManager provideDtlJobManager(@ForApplication Injector injector) {
        return new DtlJobManager(injector);
    }


    @Singleton
    @Provides
    LocationDelegate provideLocationDelegate(@ForApplication Context context) {
        return new LocationDelegate(context);
    }

    @Provides
    FeedEntityManager provideBaseFeedEntityManager(@Global EventBus eventBus) {
        return new FeedEntityManager(eventBus);
    }

    @Singleton
    @Provides
    LogoutDelegate logoutDelegate(@ForApplication Injector injector) {
        return new LogoutDelegate(injector);
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
    ClearDirectoryDelegate provideClearDirectoryDelegate(@ForApplication Context context, PhotoDAO photoDAO, SnappyRepository snappyRepository) {
        return new ClearDirectoryDelegate(context, photoDAO, snappyRepository);
    }

    @Provides
    @Singleton
    MediaPickerManager provideMediaPickerManager() {
        return new MediaPickerManager();
    }


    @Provides
    @Singleton
    GlobalConfigManager provideGlobalConfigManager(SessionHolder<UserSession> appSessionHolder,
                                                   Janet janet,
                                                   @Global EventBus eventBus) {
        return new GlobalConfigManager(appSessionHolder, janet, eventBus);
    }

    @Provides
    @Singleton
    SocialCropImageManager provideGlobalConfigManager(@ForApplication Context context,
                                                      DreamSpiceManager dreamSpiceManager) {
        return new SocialCropImageManager(context, dreamSpiceManager);
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
    TripMapManager provideTripMapManager(Janet janet, TripFilterDataProvider dataProvider, @ForApplication Context context, Gson gson) {
        return new TripMapManager(janet, dataProvider, context, gson);
    }

    @Provides
    @Singleton
    TripFilterDataProvider provideTripFilterDataProvider(@Global EventBus eventBus, SnappyRepository repository) {
        return new TripFilterDataProvider(eventBus, repository);
    }
}
