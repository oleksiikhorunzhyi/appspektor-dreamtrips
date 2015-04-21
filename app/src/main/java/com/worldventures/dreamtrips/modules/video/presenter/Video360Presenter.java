package com.worldventures.dreamtrips.modules.video.presenter;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.video.CachedVideoManager;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;
import com.worldventures.dreamtrips.modules.video.model.CachedVideo;
import com.worldventures.dreamtrips.modules.video.model.Video360;
import com.worldventures.dreamtrips.modules.video.model.Videos360;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

public class Video360Presenter extends Presenter<Video360Presenter.View> {

    private CachedVideoManager cachedVideoManager;

    public Video360Presenter(View view) {
        super(view);
    }

    @Inject
    protected SnappyRepository db;

    @Inject
    protected Injector injector;

    @Override
    public void init() {
        super.init();
        cachedVideoManager = new CachedVideoManager(db, dreamSpiceManager, context, view, injector);
        eventBus.register(cachedVideoManager);
    }

    @Override
    public void resume() {
        super.resume();
        List<Videos360> globalConfig = appSessionHolder.get().get().getGlobalConfig().getVideos360();

        List<Video360> recentVideos = globalConfig.get(1).getVideos();
        List<Video360> featuredVideos = globalConfig.get(0).getVideos();
        attachCacheToVideos(recentVideos);
        attachCacheToVideos(featuredVideos);
        attachListeners(recentVideos);
        attachListeners(featuredVideos);
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

    private List<Video360> attachCacheToVideos(List<Video360> videos) {
        for (Video360 object : videos) {
            CachedVideo e = db.getDownloadVideoEntity(object.getUid());
            object.setCacheEntity(e);
        }
        return videos;
    }


    public void onDeleteAction(CachedVideo videoEntity) {
        cachedVideoManager.onDeleteAction(videoEntity);
    }

    @Override
    public void onStop() {
        super.onStop();
        eventBus.unregister(cachedVideoManager);
    }

    private void attachListeners(List<Video360> items) {
        for (Video360 item : items) {
            CachedVideo cachedVideo = item.getCacheEntity();
            boolean failed = cachedVideo.isFailed();
            boolean inProgress = cachedVideo.getProgress() > 0;
            boolean cached = cachedVideo.isCached(context);
            if (!failed && inProgress && !cached) {
                DownloadVideoListener listener = new DownloadVideoListener(cachedVideo);
                injector.inject(listener);
                dreamSpiceManager.addListenerIfPending(InputStream.class, cachedVideo.getUuid(),
                        listener
                );
            }
        }
    }


    public interface View extends Presenter.View, CachedVideoManager.View {
        BaseArrayListAdapter getFeaturedAdapter();

        BaseArrayListAdapter getRecentAdapter();

        BaseArrayListAdapter getAllAdapter();
    }

}
