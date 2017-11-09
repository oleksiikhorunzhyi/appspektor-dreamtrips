package com.worldventures.dreamtrips.core.janet;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.janet.cache.storage.TempSessionIdProvider;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;

@Module(complete = false, library = true)
public class AutomationQaConfigModule {

   private static final String AUTOMATION_QA_API_CONFIG = "AutomationQaApiConfiguration";

   @Provides
   @Named(AUTOMATION_QA_API_CONFIG)
   Interceptor provideRequestConfigInterceptor(TempSessionIdProvider tempSessionIdProvider) {
      if (BuildConfig.QA_AUTOMATION_MODE_ENABLED) {
         return chain -> {
            Request request = chain.request();
            Headers.Builder headersBuilder = request.headers().newBuilder();

            String xSessionId = tempSessionIdProvider.getTempSessionId();
            if (!xSessionId.isEmpty()) headersBuilder.add("X-Session-id", xSessionId);

            Request newRequest = request.newBuilder().headers(headersBuilder.build()).build();
            return chain.proceed(newRequest);
         };
      } else {
         return chain -> chain.proceed(chain.request());
      }
   }

   @Provides(type = Provides.Type.SET)
   @Named(JanetUploaderyModule.JANET_UPLOADERY)
   Interceptor provideUploaderyRequestConfigInterceptor(@Named(AUTOMATION_QA_API_CONFIG) Interceptor configInterceptor) {
      return configInterceptor;
   }

   @Provides(type = Provides.Type.SET)
   @Named(MobileSdkJanetModule.API_QUALIFIER)
   Interceptor provideApiQualifierRequestConfigInterceptor(@Named(AUTOMATION_QA_API_CONFIG) Interceptor configInterceptor) {
      return configInterceptor;
   }
}
