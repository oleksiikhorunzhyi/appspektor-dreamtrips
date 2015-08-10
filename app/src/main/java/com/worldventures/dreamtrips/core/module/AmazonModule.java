package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.DreamTripsHttpClient;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class AmazonModule {

    @Provides
    public CognitoCachingCredentialsProvider provideCredProvider(Context context) {
        return new CognitoCachingCredentialsProvider(context,
                BuildConfig.AWS_ACCOUNT_ID,
                BuildConfig.COGNITO_POOL_ID,
                BuildConfig.COGNITO_ROLE_UNAUTH,
                null, Regions.US_EAST_1
        );
    }

    @Provides
    public AmazonS3Client provideAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider) {
        int connectionTimeout = 30 * 1000; // 30 secs
        //
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setConnectionTimeout(connectionTimeout);
        clientConfiguration.setSocketTimeout(connectionTimeout);

        return new AmazonS3Client(credentialsProvider, clientConfiguration, new DreamTripsHttpClient(clientConfiguration));
    }
}
