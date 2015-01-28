package com.worldventures.dreamtrips.core.uploader;

import android.os.Handler;

import com.path.android.jobqueue.JobManager;
import com.techery.spares.module.Annotations.UseModule;
import com.techery.spares.service.InjectingService;
import com.worldventures.dreamtrips.core.repository.Repository;
import com.worldventures.dreamtrips.core.uploader.job.UploadJob;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.utils.busevents.CancelUpload;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

@UseModule(UploaderModule.class)
public class UploadingService extends InjectingService {

    public static class ImageUploadAction {
        private String fileUri;
        private String title;
        private String locationName;
        private float latitude;
        private float longitude;
        private Date shotAt;
        private String originPhotoURL;
        private ArrayList<String> tags;

        public ImageUploadAction() {
        }

        public void setFileUri(String fileUri) {
            this.fileUri = fileUri;
            checkNotNull(fileUri);
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public void setLatitude(float latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(float longitude) {
            this.longitude = longitude;
        }

        public void setShotAt(Date shotAt) {
            this.shotAt = shotAt;
        }

        public void setOriginPhotoURL(String originPhotoURL) {
            this.originPhotoURL = originPhotoURL;
        }

        public void setTags(ArrayList<String> tags) {
            this.tags = tags;
        }

        public ArrayList<String> getTags() {
            return tags;
        }
    }

    @Inject
    JobManager uploadJobManager;

    @Inject
    Repository<ImageUploadTask> repository;

    @Override
    public void onCreate() {
        super.onCreate();

        this.actionRouter.on(ImageUploadAction.class, (imageUploadParams) -> {

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                ImageUploadTask uploadTask = repository.create((task) -> {
                    task.setFileUri(imageUploadParams.fileUri);
                    task.setLatitude(imageUploadParams.latitude);
                    task.setLongitude(imageUploadParams.longitude);
                    task.setLocationName(imageUploadParams.locationName);
                    task.setShotAt(imageUploadParams.shotAt);
                    task.setTitle(imageUploadParams.title);
                });

                uploadJobManager.addJob(new UploadJob(uploadTask.getTaskId()));
            }, 100);
        });
    }


}
