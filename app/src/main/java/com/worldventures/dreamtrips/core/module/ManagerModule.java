package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.DreamSpiceService;
import com.worldventures.dreamtrips.core.api.VideoCachingService;
import com.worldventures.dreamtrips.core.api.VideoCachingSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.VideoCachingDelegate;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                DownloadVideoListener.class
        },
        library = true, complete = false
)
public class ManagerModule {

    @Provides
    @Singleton
    public DreamSpiceManager provideSpiceManager(@ForApplication Injector injector) {
        return new DreamSpiceManager(DreamSpiceService.class, injector);
    }

    @Provides
    @Singleton
    public VideoCachingSpiceManager provideVideoCachingSpiceManager() {
        return new VideoCachingSpiceManager(VideoCachingService.class);
    }

    @Provides
    @Singleton
    public VideoCachingDelegate provideVideoCachingDelegate(SnappyRepository snappyRepository,
                                                            Context context,
                                                            @ForApplication Injector injector) {
        return new VideoCachingDelegate(snappyRepository, context, injector);
    }
}
