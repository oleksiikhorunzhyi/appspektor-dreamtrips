package com.worldventures.dreamtrips.modules.video;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.api.DownloadVideoListener;
import com.worldventures.dreamtrips.modules.video.event.CancelCachingVideoRequestEvent;
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
        new MaterialDialog.Builder(context)
                .title(R.string.delete_cached_video_title)
                .content(R.string.delete_cached_video_text)
                .positiveText(R.string.delete_photo_positiove)
                .negativeText(R.string.delete_photo_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        view.onDeleteAction(event.getVideoEntity());
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void onEvent(CancelCachingVideoRequestEvent event) {
        new MaterialDialog.Builder(context)
                .title(R.string.cancel_cached_video_title)
                .content(R.string.cancel_cached_video_text)
                .positiveText(R.string.cancel_photo_positiove)
                .negativeText(R.string.cancel_photo_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        view.onCancelCaching(event.getCacheEntity());
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void onDeleteAction(CachedEntity videoEntity) {
        new File(CachedEntity.getFilePath(context, videoEntity.getUrl())).delete();
        videoEntity.setProgress(0);
        db.saveDownloadVideoEntity(videoEntity);
        view.notifyItemChanged(videoEntity);
    }


    public void onCancelAction(CachedEntity cacheEntity) {
        dreamSpiceManager.cancel(InputStream.class, cacheEntity.getUuid());
        onDeleteAction(cacheEntity);
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

        void onDeleteAction(CachedEntity videoEntity);

        void onCancelCaching(CachedEntity cacheEntity);
    }
}
