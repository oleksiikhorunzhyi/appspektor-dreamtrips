package com.worldventures.dreamtrips.modules.video;

import com.thin.downloadmanager.ThinDownloadManager;
import com.worldventures.dreamtrips.modules.infopages.presenter.MembershipVideosPresenter;
import com.worldventures.dreamtrips.modules.video.request.DownloadVideoRequest;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                DownloadVideoRequest.class,
                MembershipVideosPresenter.class
        },
        complete = false,
        library = true
)
public class VideoModule {

    @Provides
    @Singleton
    public ThinDownloadManager provideThinDownloadManager() {
        return new ThinDownloadManager();
    }

}
