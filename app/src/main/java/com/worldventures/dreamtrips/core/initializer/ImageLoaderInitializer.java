package com.worldventures.dreamtrips.core.initializer;

import android.content.Context;
import android.text.TextUtils;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
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
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .diskCacheFileNameGenerator(imageUri -> TextUtils.isEmpty(imageUri) ? "" : imageUri.replaceAll("[^A-Za-z0-9.]+", ""))
                .defaultDisplayImageOptions(optionBuilder.build())
                .build();
        ImageLoader.getInstance().init(config);
    }
}
