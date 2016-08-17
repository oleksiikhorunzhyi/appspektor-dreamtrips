package com.worldventures.dreamtrips.core.initializer;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.worldventures.dreamtrips.BuildConfig;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;

public class FabricInitializer implements AppInitializer {

   private static final String TWITTER_KEY = "1DFAUsD0ZZTkCsKYoHCMzQUud";
   private static final String TWITTER_SECRET = "OflDXDujTfJiqMkdFk5z5eDV1woeNxOvWszXRLonpVbURF4hx1";

   @Inject protected Application application;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
      TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
      Crashlytics crashlyticsKit = new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.CRASHLYTICS_DISABLED)
            .build()).build();
      Fabric.with(application, new TweetComposer(), new Twitter(authConfig), crashlyticsKit);
   }
}
