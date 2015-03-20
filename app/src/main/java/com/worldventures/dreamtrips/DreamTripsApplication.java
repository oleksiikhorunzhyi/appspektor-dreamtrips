package com.worldventures.dreamtrips;

import android.content.Context;

import com.techery.spares.application.BaseApplicationWithInjector;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.worldventures.dreamtrips.core.module.DTModule;

import io.fabric.sdk.android.Fabric;

public class DreamTripsApplication extends BaseApplicationWithInjector {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "1DFAUsD0ZZTkCsKYoHCMzQUud";
    private static final String TWITTER_SECRET = "OflDXDujTfJiqMkdFk5z5eDV1woeNxOvWszXRLonpVbURF4hx1";



    @Override
    protected Object getApplicationModule() {
        return new DTModule(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TweetComposer(), new Twitter(authConfig));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}