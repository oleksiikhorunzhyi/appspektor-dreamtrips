package com.worldventures.dreamtrips.modules.video;

import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;
import com.worldventures.dreamtrips.modules.video.presenter.MembershipVideosPresenter;

import dagger.Module;

@Module(
        injects = {
                DownloadVideoListener.class,
                MembershipVideosPresenter.class,
        },
        complete = false,
        library = true
)
public class VideoModule {

}
