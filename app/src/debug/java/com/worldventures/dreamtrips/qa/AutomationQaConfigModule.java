package com.worldventures.dreamtrips.qa;

import android.os.Environment;

import com.google.gson.GsonBuilder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.janet.JanetUploaderyModule;
import com.worldventures.dreamtrips.core.janet.MobileSdkJanetModule;
import com.worldventures.dreamtrips.mobilesdk.ConfigProviders;

import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;

import static com.worldventures.dreamtrips.qa.QaConfigApiInterceptor.*;

@Module(complete = false, library = true, includes = DefaultQaConfigModule.class)
public class AutomationQaConfigModule {

   private static final String INNER = "AutomationQaApiConfiguration";
   private static final String DT_QA_CONFIG_JSON_FILENAME = "dt-qa-config.json";

   @Provides
   @Singleton
   QaConfig provideQaConfig(@Named(DefaultQaConfigModule.LABEL) QaAppConfig defaultAppConfig) {
      File sdcard = Environment.getExternalStorageDirectory();
      File config = new File(sdcard, DT_QA_CONFIG_JSON_FILENAME);
      if (BuildConfig.QA_AUTOMATION_MODE_ENABLED && config.exists()) {
         return new QaConfigLoader.FileQaConfigLoader(config, new GsonBuilder().create()).getConfig();
      } else {
         return new QaConfig(null, defaultAppConfig);
      }
   }

   @Provides
   @Named(INNER)
   ConfigurableApiHosts provideApiHosts(ConfigProviders configs) {
      return new ConfigurableApiHosts(
            configs.monolithConfig().data().url(),
            configs.monolithConfig().data().apiUploaderyUrl(),
            BuildConfig.VIDEO_MICROSERVICE_URL,
            BuildConfig.TRANSACTIONS_API_URL
      );
   }

   @Provides
   @Named(INNER)
   Interceptor provideRequestConfigInterceptor(QaConfig qaConfig, @Named(INNER) ConfigurableApiHosts apiHosts) {
      if (qaConfig.getApi() != null) {
         return new QaConfigApiInterceptor(qaConfig.getApi(), apiHosts);
      } else {
         return chain -> chain.proceed(chain.request());
      }
   }

   @Provides(type = Provides.Type.SET)
   @Named(MobileSdkJanetModule.API_QUALIFIER)
   Interceptor provideApiQualifierRequestConfigInterceptor(@Named(INNER) Interceptor configInterceptor) {
      return configInterceptor;
   }

   @Provides(type = Provides.Type.SET)
   @Named(JanetUploaderyModule.JANET_UPLOADERY)
   Interceptor provideUploaderyRequestConfigInterceptor(@Named(INNER) Interceptor configInterceptor) {
      return configInterceptor;
   }
}
