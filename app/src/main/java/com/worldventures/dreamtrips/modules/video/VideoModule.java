package com.worldventures.dreamtrips.modules.video;

import com.thin.downloadmanager.ThinDownloadManager;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;
import com.worldventures.dreamtrips.modules.video.presenter.MembershipVideosPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                DownloadVideoListener.class,
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
