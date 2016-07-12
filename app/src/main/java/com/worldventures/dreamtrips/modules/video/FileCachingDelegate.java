package com.worldventures.dreamtrips.modules.video;

import android.content.Context;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.FileDownloadSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.api.DownloadFileListener;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.File;
import java.io.InputStream;

public class FileCachingDelegate {

    private SnappyRepository db;
    private Context context;
    private Injector injector;
    private FileDownloadSpiceManager fileDownloadSpiceManager;
    private View view;

    public FileCachingDelegate(SnappyRepository db, Context context,
                               Injector injector, FileDownloadSpiceManager spiceManager) {
        this.db = db;
        this.context = context;
        this.injector = injector;
        this.fileDownloadSpiceManager = spiceManager;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void updateItem(CachedEntity entity) {
        view.notifyItemChanged(entity);
    }

    public void downloadFile(CachedEntity entity) {
        String path = CachedEntity.getFilePath(context, entity.getUrl());
        startCaching(entity, path);
    }

    public void cancelCachingFile(CachedEntity entity) {
        view.onCancelCaching(entity);
    }

    public void deleteCachedFile(CachedEntity entity) {
        view.onDeleteAction(entity);
    }

    public void onDeleteAction(CachedEntity entity) {
        deleteFile(entity);
    }

    protected void deleteFile(CachedEntity entity){
        deleteFile(entity, CachedEntity.getFilePath(context, entity.getUrl()));
    }

    protected void deleteFile(CachedEntity entity, String path){
        new File(path).delete();
        entity.setProgress(0);
        db.saveDownloadMediaEntity(entity);
        view.notifyItemChanged(entity);
    }

    public void onCancelAction(CachedEntity entity) {
        fileDownloadSpiceManager.cancel(InputStream.class, entity.getUuid());
        onDeleteAction(entity);
    }

    protected void startCaching(CachedEntity entity, String path) {
        BigBinaryRequest bigBinaryRequest = new BigBinaryRequest(entity.getUrl(), new File(path));

        DownloadFileListener requestListener = new DownloadFileListener(entity, this);
        injector.inject(requestListener);
        fileDownloadSpiceManager.cancel(InputStream.class, entity.getUuid());
        fileDownloadSpiceManager.execute(bigBinaryRequest,
                entity.getUuid(),
                DurationInMillis.ALWAYS_RETURNED,
                requestListener);
        view.notifyItemChanged(entity);
    }

    public interface View {

        void notifyItemChanged(CachedEntity entity);

        void onDeleteAction(CachedEntity entity);

        void onCancelCaching(CachedEntity entity);
    }
}
