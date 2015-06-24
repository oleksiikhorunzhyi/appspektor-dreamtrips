package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.DreamSpiceService;
import com.worldventures.dreamtrips.core.api.MediaSpiceService;
import com.worldventures.dreamtrips.core.api.MediaSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.api.UploadBucketPhotoCommand;
import com.worldventures.dreamtrips.modules.membership.api.PhoneContactRequest;
import com.worldventures.dreamtrips.modules.membership.api.UploadTemplatePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.S3ImageUploader;
import com.worldventures.dreamtrips.modules.tripsimages.api.UploadTripPhotoCommand;
import com.worldventures.dreamtrips.modules.video.VideoCachingDelegate;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                DreamSpiceManager.class,
                DreamSpiceService.class,
                MediaSpiceManager.class,
                MediaSpiceService.class,
                VideoCachingDelegate.class,
                //
                DownloadVideoListener.class,
                PhoneContactRequest.class,
                S3ImageUploader.class,
                UploadTripPhotoCommand.class,
                UploadBucketPhotoCommand.class,
                UploadTemplatePhotoCommand.class,
        },
        library = true, complete = false
)
public class ManagerModule {

    @Provides
    public DreamSpiceManager provideSpiceManager(@ForApplication Injector injector) {
        return new DreamSpiceManager(DreamSpiceService.class, injector);
    }

    @Provides
    public MediaSpiceManager provideVideoCachingSpiceManager() {
        return new MediaSpiceManager(MediaSpiceService.class);
    }

    @Provides
    public VideoCachingDelegate provideVideoCachingDelegate(SnappyRepository snappyRepository,
                                                            Context context,
                                                            @ForApplication Injector injector) {
        return new VideoCachingDelegate(snappyRepository, context, injector);
    }
}
