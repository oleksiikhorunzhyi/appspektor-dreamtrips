package com.worldventures.dreamtrips.modules.membership.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.video.VideoCachingDelegate;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;
import com.worldventures.dreamtrips.modules.video.api.MemberVideosRequest;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PresentationsPresenter extends Presenter<PresentationsPresenter.View> {

    @Inject
    protected SnappyRepository db;

    @Inject
    @ForApplication
    protected Injector injector;

    @Inject
    protected VideoCachingDelegate videoCachingDelegate;

    protected DreamSpiceAdapterController<Video> adapterController = new DreamSpiceAdapterController<Video>() {
        @Override
        public SpiceRequest<ArrayList<Video>> getRefreshRequest() {
            return new MemberVideosRequest(DreamTripsApi.TYPE_MEMBER) {
                @Override
                public ArrayList<Video> loadDataFromNetwork() throws Exception {
                    ArrayList<Video> videos = super.loadDataFromNetwork();
                    return attachCacheToVideos(videos);
                }
            };
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
        }

        @Override
        public void onFinish(LoadType type, List<Video> items, SpiceException spiceException) {
            if (adapterController != null) {
                view.finishLoading();
                attachListeners(items);
                if (spiceException != null) {
                    handleError(spiceException);
                }
            }
        }
    };

    @Override
    public void onInjected() {
        adapterController.setSpiceManager(dreamSpiceManager);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        TrackingHelper.onMemberShipVideos(getUserId());
        videoCachingDelegate.setView(this.view);
        videoCachingDelegate.setSpiceManager(videoCachingSpiceManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (view.getAdapter().getCount() == 0) {
            adapterController.setAdapter(view.getAdapter());
            adapterController.reload();
            eventBus.register(videoCachingDelegate);
        }
    }

    @Override
    public void onPause() {
        eventBus.unregister(videoCachingDelegate);
    }

    @Override
    public void dropView() {
        adapterController = null;
        super.dropView();
    }

    public DreamSpiceAdapterController<Video> getAdapterController() {
        return adapterController;
    }


    public void onDeleteAction(CachedEntity videoEntity) {
        videoCachingDelegate.onDeleteAction(videoEntity);
    }


    public void onCancelAction(CachedEntity cacheEntity) {
        videoCachingDelegate.onCancelAction(cacheEntity);
    }


    private ArrayList<Video> attachCacheToVideos(ArrayList<Video> videos) {
        if (videos != null) {
            for (Video object : videos) {
                CachedEntity e = db.getDownloadVideoEntity(object.getUid());
                object.setCacheEntity(e);
            }
        }
        return videos;
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
                    videoCachingSpiceManager.addListenerIfPending(
                            InputStream.class,
                            cachedVideo.getUuid(),
                            listener
                    );
                }
            }
        }
    }

    public interface View extends Presenter.View, VideoCachingDelegate.View {
        void startLoading();

        void finishLoading();

        IRoboSpiceAdapter<Video> getAdapter();
    }
}
