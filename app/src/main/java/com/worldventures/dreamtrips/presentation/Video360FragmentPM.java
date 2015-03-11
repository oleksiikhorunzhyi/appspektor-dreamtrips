package com.worldventures.dreamtrips.presentation;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.core.model.config.S3GlobalConfig;
import com.worldventures.dreamtrips.core.model.config.Video360;
import com.worldventures.dreamtrips.core.model.config.Videos360;

import java.util.List;

/**
 * Created by 1 on 10.03.15.
 */
public class Video360FragmentPM extends BasePresentation<Video360FragmentPM.View> {

    public Video360FragmentPM(View view) {
        super(view);
    }

    @Override
    public void resume() {
        super.resume();
        List<Videos360> globalConfig = appSessionHolder.get().get().getGlobalConfig().getVideos360();

        List<Video360> featuredVideos = globalConfig.get(0).getVideos();
        view.getFeaturedAdapter().clear();
        view.getFeaturedAdapter().addItems(featuredVideos);

        List<Video360> recentVideos = globalConfig.get(1).getVideos();
        view.getRecentAdapter().clear();
        view.getRecentAdapter().addItems(recentVideos);

    }

    public interface View extends BasePresentation.View {
        BaseArrayListAdapter getFeaturedAdapter();

        BaseArrayListAdapter getRecentAdapter();
    }
}
