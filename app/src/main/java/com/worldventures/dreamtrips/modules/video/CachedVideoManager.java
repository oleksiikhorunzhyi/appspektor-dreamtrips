package com.worldventures.dreamtrips.modules.video;

import android.content.Context;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.infopages.model.Video;
import com.worldventures.dreamtrips.modules.infopages.presenter.MembershipVideosPresenter;
import com.worldventures.dreamtrips.modules.video.event.DeleteCachedVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.model.CachedVideo;
import com.worldventures.dreamtrips.modules.video.request.DownloadVideoListener;
import com.worldventures.dreamtrips.modules.video.request.DownloadVideoPendingListener;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CachedVideoManager {

    private SnappyRepository db;
    private DreamSpiceManager dreamSpiceManager;
    private Context context;
    private MembershipVideosPresenter.View view;
    private Injector injector;


    public CachedVideoManager(SnappyRepository db,
                              DreamSpiceManager dreamSpiceManager,
                              Context context,
                              MembershipVideosPresenter.View view,
                              Injector injector) {
        this.db = db;
        this.dreamSpiceManager = dreamSpiceManager;
        this.context = context;
        this.view = view;
        this.injector = injector;
    }

    public ArrayList<Video> attachCacheToVideos(ArrayList<Video> videos) {
        for (Video object : videos) {
            CachedVideo e = db.getDownloadVideoEntity(object.getUid());
            object.setEntity(e);
        }
        return videos;
    }


    public void checkStatus(List<Video> items) {
        for (Video item : items) {
            CachedVideo cachedVideo = item.getDownloadEntity();
            if (!cachedVideo.isFailed() && cachedVideo.getProgress() > 0) {
                DownloadVideoPendingListener listener
                        = new DownloadVideoPendingListener(cachedVideo);
                injector.inject(listener);
                dreamSpiceManager.addListenerIfPending(
                        InputStream.class,
                        cachedVideo.getUuid(),
                        listener
                );
            }
        }
    }


    public void onEvent(DownloadVideoRequestEvent event) {
        CachedVideo entity = event.getCachedVideo();
        startCaching(entity);
    }

    public void onEvent(DeleteCachedVideoRequestEvent event) {
        view.showDeleteDialog(event.getVideoEntity());
    }

    public void onDeleteAction(CachedVideo videoEntity) {
        new File(videoEntity.getFilePath(context)).delete();
        videoEntity.setProgress(0);
        db.saveDownloadVideoEntity(videoEntity);
        view.notifyAdapter();
    }


    private void startCaching(CachedVideo entity) {
        BigBinaryRequest bigBinaryRequest = new BigBinaryRequest(entity.getUrl(),
                new File(entity.getFilePath(context)));

        DownloadVideoListener requestListener = new DownloadVideoListener(entity);
        injector.inject(requestListener);
        dreamSpiceManager.cancel(InputStream.class, entity.getUuid());
        dreamSpiceManager.execute(bigBinaryRequest,
                entity.getUuid(),
                DurationInMillis.ALWAYS_RETURNED,
                requestListener);
    }
}
