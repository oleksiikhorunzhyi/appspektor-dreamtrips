package com.worldventures.dreamtrips.core.initializer;

import android.app.Application;
import android.util.Log;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;

import javax.inject.Inject;


public class TwitterInitializer implements AppInitializer {

   private static final String TWITTER_KEY = "1DFAUsD0ZZTkCsKYoHCMzQUud";
   private static final String TWITTER_SECRET = "OflDXDujTfJiqMkdFk5z5eDV1woeNxOvWszXRLonpVbURF4hx1";

   @Inject protected Application application;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
      TwitterConfig config = new TwitterConfig.Builder(application)
            .logger(new DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET))
            .debug(true)
            .build();
      Twitter.initialize(config);
   }
}
