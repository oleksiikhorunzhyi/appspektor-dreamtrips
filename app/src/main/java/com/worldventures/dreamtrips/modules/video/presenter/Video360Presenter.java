package com.worldventures.dreamtrips.modules.video.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.TrackVideoStatusEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.video.VideoCachingDelegate;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;
import com.worldventures.dreamtrips.modules.video.api.MemberVideosRequest;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

public class Video360Presenter extends Presenter<Video360Presenter.View> {

    private List<Video> recentVideos;
    private List<Video> featuredVideos;

    @Inject
    protected SnappyRepository db;

    @Inject
    protected VideoCachingDelegate videoCachingDelegate;

    @Inject
    @ForApplication
    protected Injector injector;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        videoCachingDelegate.setView(this.view);
        videoCachingDelegate.setSpiceManager(videoCachingSpiceManager);
        TrackingHelper.video360(getAccountUserId());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!eventBus.isRegistered(videoCachingDelegate)) {
            eventBus.register(videoCachingDelegate);
        }
    }

    public void fillFeatured() {
        if (featuredVideos != null) {
            if (view.getFeaturedAdapter() != null) {
                view.getFeaturedAdapter().clear();
                view.getFeaturedAdapter().addItems(featuredVideos);

                view.getRecentAdapter().clear();
                view.getRecentAdapter().addItems(recentVideos);
            }
        } else {
            loadVideos();
        }
    }

    public void fillAll() {
        if (featuredVideos != null) {
            if (view != null && view.getAllAdapter() != null) {
                view.getAllAdapter().clear();
                view.getAllAdapter().addItems(featuredVideos);
                view.getAllAdapter().addItems(recentVideos);
            }
        } else {
            loadVideos();
        }
    }

    private void attachCacheToVideos(List<Video> videos) {
        if (videos != null) {
            for (Video object : videos) {
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

    private void loadVideos() {
        MemberVideosRequest memberVideosRequest = new MemberVideosRequest(DreamTripsApi.TYPE_MEMBER_360);
        doRequest(memberVideosRequest, videos -> onSuccess(videos));
    }

    private void onSuccess(List<Video> videos) {
        recentVideos = Queryable.from(videos).filter(video -> video.isRecent()).toList();
        featuredVideos = Queryable.from(videos).filter(video -> video.isFeatured()).toList();
        attachCacheToVideos(recentVideos);
        attachCacheToVideos(featuredVideos);
        attachListeners(recentVideos);
        attachListeners(featuredVideos);
        view.finishLoading();
    }

    private void attachListeners(List<Video> items) {
        if (items != null) {
            for (Video item : items) {
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
        TrackingHelper.videoAction(TrackingHelper.ACTION_360,
                getAccountUserId(), TrackingHelper.ACTION_360_LOAD_CANCELED, cacheEntity.getName());
    }

    public void onEvent(TrackVideoStatusEvent event) {
        TrackingHelper.videoAction(TrackingHelper.ACTION_360,
                getAccountUserId(), event.getAction(), event.getName());
    }


    public interface View extends Presenter.View, VideoCachingDelegate.View {
        BaseArrayListAdapter getFeaturedAdapter();

        BaseArrayListAdapter getRecentAdapter();

        BaseArrayListAdapter getAllAdapter();

        void finishLoading();
    }
}
