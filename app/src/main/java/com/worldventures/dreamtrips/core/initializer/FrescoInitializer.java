package com.worldventures.dreamtrips.core.initializer;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.core.utils.HeaderProvider;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FrescoInitializer implements AppInitializer {

   @Inject protected Context context;
   @Inject protected HeaderProvider headerProvider;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);

      final OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(chain -> {
         HeaderProvider.Header languageHeader = headerProvider.getAcceptLanguageHeader();
         Request request = chain.request().newBuilder()
               .addHeader(languageHeader.getName(), languageHeader.getValue())
               .build();
         return chain.proceed(request);
      }).build();

      SimpleProgressiveJpegConfig jpegConfig = new SimpleProgressiveJpegConfig();
      ImagePipelineConfig config = OkHttpImagePipelineConfigFactory.newBuilder(context, okHttpClient)
            .setProgressiveJpegConfig(jpegConfig)
            .setBitmapsConfig(Bitmap.Config.RGB_565)
            .setResizeAndRotateEnabledForNetwork(true)
            .build();

      Fresco.initialize(context, config);
   }
}
