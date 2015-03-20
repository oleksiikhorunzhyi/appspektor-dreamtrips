package com.worldventures.dreamtrips.core.initializer;

import android.content.Context;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Annotations.Application;
import com.techery.spares.module.Injector;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;

public class FabricInitializer implements AppInitializer {

    private static final String TWITTER_KEY = "1DFAUsD0ZZTkCsKYoHCMzQUud";
    private static final String TWITTER_SECRET = "OflDXDujTfJiqMkdFk5z5eDV1woeNxOvWszXRLonpVbURF4hx1";

    @Inject
    @Application
    Context context;

    @Override
    public void initialize(Injector injector) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(context, new TweetComposer(), new Twitter(authConfig));
    }
}
