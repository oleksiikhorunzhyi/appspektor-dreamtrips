package com.worldventures.dreamtrips.modules.gcm.service;

import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.techery.spares.service.InjectingIntentService;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager.FailureListener;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.feed.api.SubscribeDeviceCommand;

import javax.inject.Inject;

import timber.log.Timber;

public class RegistrationIntentService extends InjectingIntentService {

    @Inject
    SnappyRepository db;
    @Inject
    DreamSpiceManager spiceManager;

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        spiceManager.start(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String token = InstanceID.getInstance(this)
                    .getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Timber.d("GCM Registration Token: %s", token);
            //
            if (TextUtils.isEmpty(token) || token.equals(db.getGcmRegToken())) return;
            sendTokenToServer(token);
        } catch (Exception e) {
            Timber.e(e, "Failed to complete token refresh");
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            resetToken();
        }
    }

    private void sendTokenToServer(String token) {
        spiceManager.execute(new SubscribeDeviceCommand(token),
                v -> saveToken(token),
                FailureListener.STUB
        );
    }

    private void saveToken(String token) {
        db.setGcmRegToken(token);
    }

    private void resetToken() {
        db.setGcmRegToken(null);
    }

}
