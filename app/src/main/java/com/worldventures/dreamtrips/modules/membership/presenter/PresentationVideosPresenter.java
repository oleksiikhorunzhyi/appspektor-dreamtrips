package com.worldventures.dreamtrips.modules.membership.presenter;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.TrackVideoStatusEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.model.VideoHeader;
import com.worldventures.dreamtrips.modules.video.VideoCachingDelegate;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;
import com.worldventures.dreamtrips.modules.video.api.MemberVideosRequest;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PresentationVideosPresenter<T extends PresentationVideosPresenter.View> extends Presenter<T> {

    @Inject
    protected SnappyRepository db;

    @Inject
    @ForApplication
    protected Injector injector;

    @Inject
    protected VideoCachingDelegate videoCachingDelegate;

    protected List<Object> currentItems;

    protected MemberVideosRequest getMemberVideosRequest() {
        return new MemberVideosRequest(DreamTripsApi.TYPE_MEMBER);
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        videoCachingDelegate.setView(this.view);
    }

    @Override
    public void onResume() {
        super.onResume();
        view.startLoading();
        loadVideos();
        if (!eventBus.isRegistered(videoCachingDelegate)) {
            eventBus.register(videoCachingDelegate);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (eventBus.isRegistered(videoCachingDelegate)) {
            eventBus.unregister(videoCachingDelegate);
        }
    }

    public void onDeleteAction(CachedEntity videoEntity) {
        videoCachingDelegate.onDeleteAction(videoEntity);
    }


    public void onCancelAction(CachedEntity cacheEntity) {
        videoCachingDelegate.onCancelAction(cacheEntity);
        TrackingHelper.videoAction(TrackingHelper.ACTION_MEMBERSHIP,
                getAccountUserId(), TrackingHelper.ACTION_MEMBERSHIP_LOAD_CANCELED, cacheEntity.getName());
    }

    public void reload() {
        loadVideos();
    }

    private void loadVideos() {
        doRequest(getMemberVideosRequest(), videos -> {
            view.finishLoading();
            attachCacheToVideos(videos);
            addCategories(videos);
            attachListeners(videos);
        });
    }

    private void attachCacheToVideos(List<Video> videos) {
        Queryable.from(videos).forEachR(video -> {
            CachedEntity e = db.getDownloadVideoEntity(video.getUid());
            video.setCacheEntity(e);

            if (TextUtils.isEmpty(video.getCategory())) doOnVideoCategoryEmpty(video);
        });
    }

    protected void doOnVideoCategoryEmpty(Video video) {
        video.setCategory(context.getString(R.string.recent_videos));
    }

    protected void addCategories(List<Video> videos) {
        currentItems = new ArrayList<>();

        List<String> categories = Queryable.from(videos)
                .map(Video::getCategory)
                .distinct().toList();

        Queryable.from(categories).forEachR(category -> addCategoryHeader(category,
                videos, categories.indexOf(category)));

        view.getAdapter().clear();
        view.getAdapter().addItems(currentItems);
    }

    protected void addCategoryHeader(String category, List<Video> videos, int categoryIndex) {
        currentItems.add(new VideoHeader(category));
        currentItems.addAll(Queryable.from(videos).filter(video ->
                video.getCategory().equals(category)).toList());
    }


    private void attachListeners(List<Video> items) {
        Queryable.from(items).forEachR(item -> {
            CachedEntity cachedVideo = item.getCacheEntity();
            boolean failed = cachedVideo.isFailed();
            boolean inProgress = cachedVideo.getProgress() > 0;
            boolean cached = cachedVideo.isCached(context);
            if (!failed && inProgress && !cached) {
                DownloadVideoListener listener = new DownloadVideoListener(cachedVideo);
                injector.inject(listener);
                videoDownloadSpiceManager.addListenerIfPending(
                        InputStream.class,
                        cachedVideo.getUuid(),
                        listener
                );
            }
        });
    }

    public void onEvent(TrackVideoStatusEvent event) {
        TrackingHelper.videoAction(TrackingHelper.ACTION_MEMBERSHIP,
                getAccountUserId(), event.getAction(), event.getName());
    }

    @Override
    public void onStop() {
        super.onStop();
        eventBus.unregister(videoCachingDelegate);
    }

    public interface View extends Presenter.View, VideoCachingDelegate.View {
        void startLoading();

        void finishLoading();

        BaseArrayListAdapter<Object> getAdapter();
    }
}
