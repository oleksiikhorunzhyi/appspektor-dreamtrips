package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.regions.Regions;
import com.worldventures.dreamtrips.BuildConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class AmazonModule {

    @Provides
    public CognitoCachingCredentialsProvider provideCredProvider(Context context) {
        return new CognitoCachingCredentialsProvider(
                context,
                BuildConfig.AWS_ACCOUNT_ID,
                BuildConfig.COGNITO_POOL_ID,
                BuildConfig.COGNITO_ROLE_UNAUTH,
                null,
                Regions.US_EAST_1);
    }

    @Provides
    @Singleton
    public TransferManager provideTransferManager(CognitoCachingCredentialsProvider credentialsProvider) {
        return new TransferManager(credentialsProvider);
    }
}
