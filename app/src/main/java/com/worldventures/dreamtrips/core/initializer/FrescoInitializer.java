package com.worldventures.dreamtrips.core.initializer;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.squareup.okhttp.OkHttpClient;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class FrescoInitializer implements AppInitializer {

    @Inject
    protected Context context;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);

        SimpleProgressiveJpegConfig jpegConfig = new SimpleProgressiveJpegConfig();
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory.newBuilder(context, new OkHttpClient())
                .setProgressiveJpegConfig(jpegConfig)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .setResizeAndRotateEnabledForNetwork(true)
                .build();

        Fresco.initialize(context, config);
    }
}
