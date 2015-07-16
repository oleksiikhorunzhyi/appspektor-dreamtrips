package com.worldventures.dreamtrips.core.api;

import android.util.Log;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.UploadBucketPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;

import javax.inject.Inject;

import roboguice.util.temp.Ln;

public class MediaSpiceManager extends SpiceManager {

    public MediaSpiceManager(Class<? extends SpiceService> spiceServiceClass) {
        super(spiceServiceClass);
        Ln.getConfig().setLoggingLevel(BuildConfig.DEBUG ? Log.DEBUG : Log.ERROR);
    }

    public void uploadPhoto(BucketPhotoUploadTask task, BucketItem bucketItem, BucketTabsPresenter.BucketType type,
                            Injector injector,
                            DreamSpiceManager.FailureListener failureListener) {
        UploadBucketPhotoCommand uploadBucketPhotoCommand = new UploadBucketPhotoCommand(task, bucketItem, type, injector);
        execute(uploadBucketPhotoCommand, new RequestListener<BucketPhoto>() {
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
}
