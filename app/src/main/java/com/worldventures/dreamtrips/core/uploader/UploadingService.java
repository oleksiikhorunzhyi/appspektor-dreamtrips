package com.worldventures.dreamtrips.core.uploader;

import com.path.android.jobqueue.JobManager;
import com.techery.spares.module.Annotations.UseModule;
import com.techery.spares.service.InjectingService;
import com.worldventures.dreamtrips.core.repository.Repository;
import com.worldventures.dreamtrips.core.uploader.job.UploadJob;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

@UseModule(UploaderModule.class)
public class UploadingService extends InjectingService {

    public static class ImageUploadAction {
        final String filePath;

        public ImageUploadAction(String filePath) {
            checkNotNull(filePath);
            this.filePath = filePath;
        }
    }

    static protected Class getCurrentClass() {
        return UploadingService.class;
    }

    @Inject
    JobManager uploadJobManager;

    @Inject
    transient Repository<ImageUploadTask> repository;

    @Override
    public void onCreate() {
        super.onCreate();

        this.actionRouter.on(ImageUploadAction.class, (imageUploadParams) -> {
            repository.create((task) -> {
                task.setFilePath(imageUploadParams.filePath);

                this.uploadJobManager.addJob(new UploadJob(task.getTaskId()));
            });
        });
    }
}
