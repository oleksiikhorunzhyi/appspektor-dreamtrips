package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.core.model.config.Video360;
import com.worldventures.dreamtrips.core.model.config.Videos360;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;

import java.util.List;

/**
 * Created by 1 on 10.03.15.
 */
public class Video360FragmentPM extends BasePresenter<Video360FragmentPM.View> {

    public Video360FragmentPM(View view) {
        super(view);
    }

    @Override
    public void resume() {
        super.resume();
        List<Videos360> globalConfig = appSessionHolder.get().get().getGlobalConfig().getVideos360();

        List<Video360> recentVideos = globalConfig.get(1).getVideos();
        List<Video360> featuredVideos = globalConfig.get(0).getVideos();

        if (view.getFeaturedAdapter() != null) {
            view.getFeaturedAdapter().clear();
            view.getFeaturedAdapter().addItems(featuredVideos);

            view.getRecentAdapter().clear();
            view.getRecentAdapter().addItems(recentVideos);
        }
        if (view.getAllAdapter() != null) {
            view.getAllAdapter().clear();
            view.getAllAdapter().addItems(featuredVideos);
            view.getAllAdapter().addItems(recentVideos);
        }
    }

    public interface View extends BasePresenter.View {
        BaseArrayListAdapter getFeaturedAdapter();

        BaseArrayListAdapter getRecentAdapter();

        BaseArrayListAdapter getAllAdapter();
    }
}
