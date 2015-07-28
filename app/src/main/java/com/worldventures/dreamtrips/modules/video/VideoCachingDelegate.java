package com.worldventures.dreamtrips.modules.video;

import android.content.Context;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.VideoDownloadSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;
import com.worldventures.dreamtrips.modules.video.event.CancelCachingVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DeleteCachedVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.File;
import java.io.InputStream;

public class VideoCachingDelegate {

    private SnappyRepository db;
    private Context context;
    private Injector injector;
    private VideoDownloadSpiceManager videoDownloadSpiceManager;
    private View view;

    public VideoCachingDelegate(SnappyRepository db,
                                Context context,
                                Injector injector, VideoDownloadSpiceManager spiceManager) {
        this.db = db;
        this.context = context;
        this.injector = injector;
        this.videoDownloadSpiceManager = spiceManager;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void onEvent(DownloadVideoRequestEvent event) {
        CachedEntity entity = event.getCachedVideo();
        startCaching(entity);
    }

    public void onEvent(DeleteCachedVideoRequestEvent event) {
        view.onDeleteAction(event.getVideoEntity());
    }

    public void onEvent(CancelCachingVideoRequestEvent event) {
        view.onCancelCaching(event.getCacheEntity());
    }

    public void onDeleteAction(CachedEntity videoEntity) {
        new File(CachedEntity.getFilePath(context, videoEntity.getUrl())).delete();
        videoEntity.setProgress(0);
        db.saveDownloadVideoEntity(videoEntity);
        view.notifyItemChanged(videoEntity);
    }


    public void onCancelAction(CachedEntity cacheEntity) {
        videoDownloadSpiceManager.cancel(InputStream.class, cacheEntity.getUuid());
        onDeleteAction(cacheEntity);
    }

    private void startCaching(CachedEntity entity) {
        BigBinaryRequest bigBinaryRequest = new BigBinaryRequest(entity.getUrl(),
                new File(CachedEntity.getFilePath(context, entity.getUrl())));

        DownloadVideoListener requestListener = new DownloadVideoListener(entity);
        injector.inject(requestListener);
        videoDownloadSpiceManager.cancel(InputStream.class, entity.getUuid());
        videoDownloadSpiceManager.execute(bigBinaryRequest,
                entity.getUuid(),
                DurationInMillis.ALWAYS_RETURNED,
                requestListener);
        view.notifyItemChanged(entity);
    }

    public interface View {
        void notifyItemChanged(CachedEntity videoEntity);

        void onDeleteAction(CachedEntity videoEntity);

        void onCancelCaching(CachedEntity cacheEntity);
    }
}
