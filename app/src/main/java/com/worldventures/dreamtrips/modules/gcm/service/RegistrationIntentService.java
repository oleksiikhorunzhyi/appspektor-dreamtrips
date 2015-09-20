package com.worldventures.dreamtrips.modules.gcm.service;

import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.techery.spares.service.InjectingIntentService;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import javax.inject.Inject;

import timber.log.Timber;

public class RegistrationIntentService extends InjectingIntentService {

    private static final String TAG = "RegIntentService";

    @Inject
    SnappyRepository db;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Timber.i(TAG, "GCM Registration Token: " + token);

            if ((token != null && !token.equals(db.getGcmRegToken()))
                    || !db.getGcmRegIdPersisted()) {
                sendRegistrationToServer(token);
            }
        } catch (Exception e) {
            Timber.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            db.setGcmRegIdPersisted(false);
            db.setGcmRegToken("");
        }
    }

    /**
     * Persist registration to DreamTrip server.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO : notify DreamTrips server about this device and it's registration ID
        if (true) { // if successfully persisted to server
            db.setGcmRegIdPersisted(true);
            db.setGcmRegToken(token);
        }
    }
}
