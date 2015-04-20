package com.worldventures.dreamtrips.modules.video;

import com.thin.downloadmanager.ThinDownloadManager;
import com.worldventures.dreamtrips.modules.infopages.presenter.MembershipVideosPresenter;
import com.worldventures.dreamtrips.modules.video.request.DownloadVideoListener;
import com.worldventures.dreamtrips.modules.video.request.DownloadVideoPendingListener;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                DownloadVideoListener.class,
                DownloadVideoPendingListener.class,
                MembershipVideosPresenter.class,
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
