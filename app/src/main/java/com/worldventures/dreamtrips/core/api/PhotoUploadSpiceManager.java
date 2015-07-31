package com.worldventures.dreamtrips.core.api;

import android.content.Context;
import android.os.Handler;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadFailedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadFinished;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.UploadBucketPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.feed.api.UploadPostPhotoCommand;
import com.worldventures.dreamtrips.modules.feed.event.PostPhotoUploadFailed;
import com.worldventures.dreamtrips.modules.feed.event.PostPhotoUploadFinished;
import com.worldventures.dreamtrips.modules.tripsimages.api.UploadTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.net.URL;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class PhotoUploadSpiceManager extends SpiceManager {


    @Inject
    @Global
    protected EventBus eventBus;

    @Inject
    protected Context context;

    private Injector injector;


    public PhotoUploadSpiceManager(Class<? extends SpiceService> spiceServiceClass, Injector injector) {
        super(spiceServiceClass);
        this.injector = injector;
        injector.inject(this);
    }

    public void uploadPhoto(BucketPhotoUploadTask task, BucketItem bucketItem, BucketTabsPresenter.BucketType type,
                            DreamSpiceManager.FailureListener failureListener) {
        UploadBucketPhotoCommand uploadBucketPhotoCommand = new UploadBucketPhotoCommand(task, bucketItem, type, injector);
        execute(uploadBucketPhotoCommand, task.getTaskId(), DurationInMillis.ALWAYS_EXPIRED,
                new RequestListener<BucketPhoto>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        if (failureListener != null)
                            failureListener.handleError(spiceException);
                    }

                    @Override
                    public void onRequestSuccess(BucketPhoto bucketPhoto) {
                        if (bucketPhoto != null) {
                            TrackingHelper.bucketPhotoAction(TrackingHelper.ACTION_BUCKET_PHOTO_UPLOAD_FINISH,
                                    task.getSelectionType(), bucketItem.getType());
                        }
                    }
                });
    }

    public void uploadPostPhoto(ImageUploadTask task) {
        UploadPostPhotoCommand requst = new UploadPostPhotoCommand(task, injector);
        execute(requst, new RequestListener<URL>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                new Handler().postDelayed(() -> eventBus.post(new PostPhotoUploadFailed(task.getTaskId())), 300);
            }

            @Override
            public void onRequestSuccess(URL url) {
                if (url != null)
                    new Handler().postDelayed(() -> eventBus.post(new PostPhotoUploadFinished(task.getTaskId(),
                            url.toString())), 300);
            }
        });
    }

    public void uploadPhoto(ImageUploadTask task) {
        try {
            UploadTripPhotoCommand request = new UploadTripPhotoCommand(task, injector);
            execute(request, new RequestListener<Photo>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    new Handler().postDelayed(() -> eventBus.post(new PhotoUploadFailedEvent(task.getTaskId())), 300);
                }

                @Override
                public void onRequestSuccess(Photo photo) {
                    //nothing to do here
                }
            });
        } catch (Exception e) {
            Timber.e(e, "Can't upload photo");
            new Handler().postDelayed(() -> eventBus.post(new PhotoUploadFailedEvent(task.getTaskId())), 300);

        }
    }
}
