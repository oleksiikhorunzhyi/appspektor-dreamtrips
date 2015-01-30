package com.worldventures.dreamtrips.core.uploader;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.regions.Regions;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.di.DependencyInjector;
import com.techery.spares.module.InjectingServiceModule;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.module.ApiModule;
import com.worldventures.dreamtrips.core.module.DTModule;
import com.worldventures.dreamtrips.core.repository.Repository;
import com.worldventures.dreamtrips.core.uploader.job.UploadJob;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;

import javax.inject.Singleton;

import dagger.Provides;
import io.realm.Realm;
import retrofit.RestAdapter;

@dagger.Module(
        addsTo = DTModule.class,
        injects = {
                UploadJob.class,
                UploadingService.class
        },
        includes = {
                InjectingServiceModule.class,
                ApiModule.class
        }
)
public class UploaderModule {

    @Provides
    CognitoCachingCredentialsProvider provideCredProvider(Context context) {
        return new CognitoCachingCredentialsProvider(
                context,
                Constants.AWS_ACCOUNT_ID,
                Constants.COGNITO_POOL_ID,
                Constants.COGNITO_ROLE_UNAUTH,
                null,
                Regions.US_EAST_1);
    }

    @Provides
    @Singleton
    TransferManager provideTransferManager(CognitoCachingCredentialsProvider credentialsProvider) {
        return new TransferManager(credentialsProvider);
    }

    @Provides
    DependencyInjector provideDependencyInjector(@InjectingServiceModule.Service Injector injector) {
        return injector::inject;
    }

    @Provides
    Configuration provideJobManagerConfiguration(Context context, DependencyInjector injector) {
        return new Configuration.Builder(context)
                .customLogger(new Logger())
                .injector(injector)
                .minConsumerCount(1)
                .maxConsumerCount(5)
                .loadFactor(3)
                .consumerKeepAlive(15)
                .id("Uploading Job Manager")
                .build();
    }

    @Provides
    @Singleton
    JobManager provideJobManager(Context context, Configuration configuration) {
        return new JobManager(context, configuration);
    }

    @Provides
    UploadingFileManager provideUploadingFileManager(Context context) {
        return new UploadingFileManager(context);
    }

    @Provides
    Repository<ImageUploadTask> provideImageUploadRepository(Realm realm) {
        return new ImageUploadsRepository(realm);
    }

    @Provides
    UploadingAPI provideUploadingAPI(RestAdapter adapter) {
        return adapter.create(UploadingAPI.class);
    }
}
