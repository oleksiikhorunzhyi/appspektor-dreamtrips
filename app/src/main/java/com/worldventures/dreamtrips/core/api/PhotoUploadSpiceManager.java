package com.worldventures.dreamtrips.core.api;

import android.content.Context;
import android.os.Handler;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.utils.events.PhotoUploadFailedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.UploadTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

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
