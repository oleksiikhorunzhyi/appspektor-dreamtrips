package com.worldventures.dreamtrips.modules.video;

import android.content.Context;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;
import com.worldventures.dreamtrips.modules.video.event.DeleteCachedVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.File;
import java.io.InputStream;

public class CachedVideoManager {

    private SnappyRepository db;
    private DreamSpiceManager dreamSpiceManager;
    private Context context;
    private View view;
    private Injector injector;


    public CachedVideoManager(SnappyRepository db,
                              DreamSpiceManager dreamSpiceManager,
                              Context context,
                              View view,
                              Injector injector) {
        this.db = db;
        this.dreamSpiceManager = dreamSpiceManager;
        this.context = context;
        this.view = view;
        this.injector = injector;
    }

    public void onEvent(DownloadVideoRequestEvent event) {
        CachedEntity entity = event.getCachedVideo();
        startCaching(entity);
    }

    public void onEvent(DeleteCachedVideoRequestEvent event) {
        view.showDeleteDialog(event.getVideoEntity());
    }

    public void onDeleteAction(CachedEntity videoEntity) {
        new File(CachedEntity.getFilePath(context, videoEntity.getUrl())).delete();
        videoEntity.setProgress(0);
        db.saveDownloadVideoEntity(videoEntity);
        view.notifyItemChanged(videoEntity);
    }


    private void startCaching(CachedEntity entity) {
        BigBinaryRequest bigBinaryRequest = new BigBinaryRequest(entity.getUrl(),
                new File(CachedEntity.getFilePath(context, entity.getUrl())));

        DownloadVideoListener requestListener = new DownloadVideoListener(entity);
        injector.inject(requestListener);
        dreamSpiceManager.cancel(InputStream.class, entity.getUuid());
        dreamSpiceManager.execute(bigBinaryRequest,
                entity.getUuid(),
                DurationInMillis.ALWAYS_RETURNED,
                requestListener);
    }

    public interface View {
        void notifyItemChanged(CachedEntity videoEntity);

        void showDeleteDialog(CachedEntity cachedVideo);
    }
}
