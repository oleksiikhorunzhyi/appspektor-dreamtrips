package com.worldventures.dreamtrips;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.worldventures.dreamtrips.core.module.DTModule;
import com.worldventures.dreamtrips.utils.Logs;
import com.worldventures.dreamtrips.view.activity.Injector;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public class DTApplication extends Application implements Injector {

    public static final String DEFAULT_TAG = "DreamApp";
    private ObjectGraph objectGraph;

    public static DTApplication get(Context context) {
        return (DTApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logs.init(DEFAULT_TAG, BuildConfig.DEBUG);
        initGraph();
        initImageLoader();


    }

    private void initImageLoader() {
        DisplayImageOptions.Builder optionBuilder = new DisplayImageOptions.Builder();
        optionBuilder.cacheOnDisk(true);
        optionBuilder.cacheInMemory(true);
        optionBuilder.bitmapConfig(Bitmap.Config.RGB_565);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(3)
                .writeDebugLogs()
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .diskCacheSize(50 * 1024 * 1024)
                .defaultDisplayImageOptions(optionBuilder.build())
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void initGraph() {
        objectGraph = ObjectGraph.create(getModules().toArray());
    }

    private List<Object> getModules() {
        return Arrays.asList(new DTModule(this));
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }

    public ObjectGraph plus(Object... obj) {
        return objectGraph.plus(obj);
    }
}
