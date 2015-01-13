package com.worldventures.dreamtrips.core.uploader;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.path.android.jobqueue.JobManager;
import com.techery.spares.module.Annotations.UseModule;
import com.techery.spares.service.InjectingService;
import com.worldventures.dreamtrips.core.uploader.job.UploadJob;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

@UseModule(UploaderModule.class)
public class UploadingService extends InjectingService {

    private static final String ACTION_UPLOAD = "com.worldventures.dreamtrips.core.uploader.action.UPLOAD";
    private static final String EXTRA_UPLOADING_FILE_PATH = "com.worldventures.dreamtrips.core.uploader.extra.UPLOADING_FILE_PATH";

    @Inject
    JobManager uploadJobManager;

    public static void start(Context context) {
        Intent intent = new Intent(context, UploadingService.class);
        context.startService(intent);
    }

    public static void addUploading(Context context, String filePath) {
        Intent intent = new Intent(context, UploadingService.class);
        intent.setAction(ACTION_UPLOAD);
        intent.putExtra(EXTRA_UPLOADING_FILE_PATH, filePath);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkNotNull(intent);

        final String action = intent.getAction();

        if (action != null) {
            if (ACTION_UPLOAD.equals(action)) {
                final String filePath = intent.getStringExtra(EXTRA_UPLOADING_FILE_PATH);

                this.uploadJobManager.addJob(new UploadJob(filePath));
            }
        }

        return Service.START_STICKY;
    }
}
