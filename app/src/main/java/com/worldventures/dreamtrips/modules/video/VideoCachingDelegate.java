package com.worldventures.dreamtrips.modules.video;

import android.content.Context;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.VideoDownloadSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;
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

    public void updateItem(CachedEntity entity) {
        view.notifyItemChanged(entity);
    }

    public void downloadVideo(CachedEntity entity) {
        startCaching(entity);
    }

    public void deleteCachedVideo(CachedEntity entity) {
        view.onDeleteAction(entity);
    }

    public void cancelCachingVideo(CachedEntity entity) {
        view.onCancelCaching(entity);
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

        DownloadVideoListener requestListener = new DownloadVideoListener(entity, this);
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
