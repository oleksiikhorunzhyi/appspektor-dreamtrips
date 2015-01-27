package com.worldventures.dreamtrips.core.initializer;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class ImageLoaderInitializer implements AppInitializer {

    @Inject
    Context context;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);

        DisplayImageOptions.Builder optionBuilder = new DisplayImageOptions.Builder();
        optionBuilder.cacheOnDisk(true);
        optionBuilder.cacheInMemory(true);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this.context)
                .threadPoolSize(3)
                //.writeDebugLogs()
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSizePercentage(30)
                .diskCacheSize(50 * 1024 * 1024)
                .defaultDisplayImageOptions(optionBuilder.build())
                .build();
        ImageLoader.getInstance().init(config);
    }
}
