package com.worldventures.dreamtrips.modules.video.presenter;

import android.content.Context;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.video.CachedVideoManager;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;
import com.worldventures.dreamtrips.modules.video.api.MemberVideosRequest;
import com.worldventures.dreamtrips.modules.video.model.CachedVideo;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MembershipVideosPresenter extends Presenter<MembershipVideosPresenter.View> {

    @Inject
    protected LoaderFactory loaderFactory;
    @Inject
    protected SharedServicesApi sp;
    @Inject
    protected Context context;
    @Inject
    protected SnappyRepository db;
    @Inject
    protected Injector injector;

    protected CachedVideoManager cachedVideoManager;

    protected RoboSpiceAdapterController<Video> adapterController
            = new RoboSpiceAdapterController<Video>() {
        @Override
        public SpiceRequest<ArrayList<Video>> getRefreshRequest() {
            return new MemberVideosRequest() {
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
            view.finishLoading();
            attachListeners(items);
        }
    };


    public MembershipVideosPresenter(View view) {
        super(view);
    }

    public void actionEnroll() {
        TrackingHelper.enroll(getUserId());
        activityRouter.openEnroll();
    }

    @Override
    public void init() {
        super.init();
        TrackingHelper.onMemberShipVideos(getUserId());
        cachedVideoManager = new CachedVideoManager(db, dreamSpiceManager, context, view, injector);
        eventBus.register(cachedVideoManager);
    }

    @Override
    public void resume() {
        super.resume();
        adapterController.setSpiceManager(dreamSpiceManager);
        adapterController.setAdapter(view.getAdapter());
        adapterController.reload();
    }

    public RoboSpiceAdapterController<Video> getAdapterController() {
        return adapterController;
    }


    public void onDeleteAction(CachedVideo videoEntity) {
        cachedVideoManager.onDeleteAction(videoEntity);
    }


    private ArrayList<Video> attachCacheToVideos(ArrayList<Video> videos) {
        for (Video object : videos) {
            CachedVideo e = db.getDownloadVideoEntity(object.getUid());
            object.setEntity(e);
        }
        return videos;
    }


    private void attachListeners(List<Video> items) {
        for (Video item : items) {
            CachedVideo cachedVideo = item.getCacheEntity();
            if (!cachedVideo.isFailed() && cachedVideo.getProgress() > 0
                    && !cachedVideo.isCached(context)) {
                DownloadVideoListener listener
                        = new DownloadVideoListener(cachedVideo);
                injector.inject(listener);
                dreamSpiceManager.addListenerIfPending(
                        InputStream.class,
                        cachedVideo.getUuid(),
                        listener
                );
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        eventBus.unregister(cachedVideoManager);
    }

    public interface View extends Presenter.View, CachedVideoManager.View {
        void startLoading();

        void finishLoading();

        IRoboSpiceAdapter<Video> getAdapter();
    }
}
