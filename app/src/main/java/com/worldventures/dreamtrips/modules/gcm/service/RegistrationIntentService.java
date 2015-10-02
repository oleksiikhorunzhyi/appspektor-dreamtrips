package com.worldventures.dreamtrips.modules.gcm.service;

import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.techery.spares.service.InjectingIntentService;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.feed.model.notification.PushSubscription;

import javax.inject.Inject;

import timber.log.Timber;

public class RegistrationIntentService extends InjectingIntentService {

    @Inject
    SnappyRepository db;
    @Inject
    DreamTripsApi dreamTripsApi;

    public RegistrationIntentService() {
        super("RegIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Timber.d("GCM Registration Token: %s", token);

            String tokenInRepository = db.getGcmRegToken();
            if (!TextUtils.isEmpty(token) && !token.equals(tokenInRepository)) {
                sendRegistrationToServer(token);
            }

        } catch (Exception e) {
            Timber.e(e, "Failed to complete token refresh");
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            db.setGcmRegToken(null);
        }
    }

    /**
     * Persist registration to DreamTrip server.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        dreamTripsApi.subscribeDevice(new PushSubscription("android", token,
                BuildConfig.VERSION_NAME, Build.VERSION.CODENAME));
        db.setGcmRegToken(token);
    }
}
