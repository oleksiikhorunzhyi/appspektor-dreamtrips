package com.worldventures.dreamtrips.modules.video.presenter;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.video.VideoCachingDelegate;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video360;
import com.worldventures.dreamtrips.modules.video.model.Videos360;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

public class Video360Presenter extends Presenter<Video360Presenter.View> {


    private List<Video360> recentVideos;
    private List<Video360> featuredVideos;

    @Inject
    protected SnappyRepository db;

    @Inject
    protected VideoCachingDelegate videoCachingDelegate;

    @Inject
    protected Injector injector;

    public Video360Presenter(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        videoCachingDelegate.setView(view);
        videoCachingDelegate.setSpiceManager(videoCachingSpiceManager);

        List<Videos360> globalConfig = appSessionHolder.get().get().getGlobalConfig().getVideos360();

        recentVideos = globalConfig.get(1).getVideos();
        featuredVideos = globalConfig.get(0).getVideos();
        attachCacheToVideos(recentVideos);
        attachCacheToVideos(featuredVideos);
        attachListeners(recentVideos);
        attachListeners(featuredVideos);
    }

    @Override
    public void resume() {
        super.resume();
        if (!eventBus.isRegistered(videoCachingDelegate)) {
            eventBus.register(videoCachingDelegate);
        }
    }

    public void fillFeatured() {
        if (view.getFeaturedAdapter() != null) {
            view.getFeaturedAdapter().clear();
            view.getFeaturedAdapter().addItems(featuredVideos);

            view.getRecentAdapter().clear();
            view.getRecentAdapter().addItems(recentVideos);
        }
    }

    public void fillAll() {
        if (view.getAllAdapter() != null) {
            view.getAllAdapter().clear();
            view.getAllAdapter().addItems(featuredVideos);
            view.getAllAdapter().addItems(recentVideos);
        }
    }

    private void attachCacheToVideos(List<Video360> videos) {
        if (videos != null) {
            for (Video360 object : videos) {
                CachedEntity e = db.getDownloadVideoEntity(object.getUid());
                object.setCacheEntity(e);
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        eventBus.unregister(videoCachingDelegate);
    }

    private void attachListeners(List<Video360> items) {
        if (items != null) {
            for (Video360 item : items) {
                CachedEntity cachedVideo = item.getCacheEntity();
                boolean failed = cachedVideo.isFailed();
                boolean inProgress = cachedVideo.getProgress() > 0;
                boolean cached = cachedVideo.isCached(context);
                if (!failed && inProgress && !cached) {
                    DownloadVideoListener listener = new DownloadVideoListener(cachedVideo);
                    injector.inject(listener);
                    videoCachingSpiceManager.addListenerIfPending(InputStream.class, cachedVideo.getUuid(),
                            listener
                    );
                }
            }
        }
    }

    public void onDeleteAction(CachedEntity videoEntity) {
        videoCachingDelegate.onDeleteAction(videoEntity);
    }


    public void onCancelAction(CachedEntity cacheEntity) {
        videoCachingDelegate.onCancelAction(cacheEntity);
    }

    public interface View extends Presenter.View, VideoCachingDelegate.View {
        BaseArrayListAdapter getFeaturedAdapter();

        BaseArrayListAdapter getRecentAdapter();

        BaseArrayListAdapter getAllAdapter();
    }
}
