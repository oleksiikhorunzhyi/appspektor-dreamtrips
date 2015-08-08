package com.worldventures.dreamtrips.core.api;

import android.content.Context;
import android.util.Log;

import com.amazonaws.services.s3.AmazonS3Client;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.api.CancelUploadCommand;
import com.worldventures.dreamtrips.modules.common.api.UploadToS3Command;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import roboguice.util.temp.Ln;

public class PhotoUploadingSpiceManager extends SpiceManager {

    @Inject
    SnappyRepository snapper;
    @Inject
    AmazonS3Client amazonS3;
    @Inject
    Context context;
    @Inject
    @Global
    EventBus eventBus;

    /**
     * Creates a {@link SpiceManager}. Typically this occurs in the construction
     * of an Activity or Fragment. This method will check if the service to bind
     * to has been properly declared in AndroidManifest.
     *
     * @param spiceServiceClass the service class to bind to.
     */
    public PhotoUploadingSpiceManager(Class<? extends SpiceService> spiceServiceClass, Injector injector) {
        super(spiceServiceClass);
        injector.inject(this);

        Ln.getConfig().setLoggingLevel(Log.ERROR);
    }

    public void uploadPhotoToS3(UploadTask uploadTask) {
        UploadToS3Command uploadToS3Command = new UploadToS3Command(context, amazonS3,
                eventBus, snapper, uploadTask);
        execute(uploadToS3Command, uploadTask.getFilePath(), DurationInMillis.ALWAYS_RETURNED,
                null);
    }

    public void cancelUploading(UploadTask uploadTask) {
        CancelUploadCommand command = new CancelUploadCommand(amazonS3, snapper, uploadTask);
        execute(command, null);
    }
}
