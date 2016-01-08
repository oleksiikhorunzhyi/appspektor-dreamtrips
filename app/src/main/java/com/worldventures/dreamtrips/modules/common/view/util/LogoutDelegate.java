package com.worldventures.dreamtrips.modules.common.view.util;

import android.content.Context;

import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.storage.MessengerDatabase;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.BadgeUpdater;
import com.worldventures.dreamtrips.core.utils.DeleteTokenGcmTask;
import com.worldventures.dreamtrips.modules.common.api.LogoutCommand;
import com.worldventures.dreamtrips.modules.feed.api.UnsubscribeDeviceCommand;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;

import javax.inject.Inject;

import timber.log.Timber;

public class LogoutDelegate {

    @Inject
    protected Context context;
    @Inject
    protected SnappyRepository snappyRepository;
    @Inject
    protected SessionHolder<UserSession> appSessionHolder;
    @Inject
    protected NotificationDelegate notificationDelegate;
    @Inject
    protected BadgeUpdater badgeUpdater;
    //
    protected DreamSpiceManager dreamSpiceManager;

    private OnLogoutSuccessListener onLogoutSuccessListener;

    public LogoutDelegate(Injector injector) {
        injector.inject(this);
    }

    public void setDreamSpiceManager(DreamSpiceManager dreamSpiceManager) {
        this.dreamSpiceManager = dreamSpiceManager;
    }

    public void logout() {
        MessengerConnector.getInstance().disconnect();

        String token = snappyRepository.getGcmRegToken();
        if (token != null) {
            dreamSpiceManager.execute(new UnsubscribeDeviceCommand(token),
                    aVoid -> deleteTokenInGcm(),
                    spiceException -> {
                        Timber.e(spiceException, "Unsubscribe failed");
                        deleteTokenInGcm();
                    });
        } else {
            deleteSession();
        }
    }

    private void deleteTokenInGcm() {
        new DeleteTokenGcmTask(context, (task, removeGcmTokenSucceed) -> {
            deleteSession();
        }).execute();
    }

    private void deleteSession() {
        dreamSpiceManager.execute(new LogoutCommand(),
                aVoid -> clearUserDataAndFinish(),
                aVoid -> clearUserDataAndFinish());
    }

    private void clearUserDataAndFinish() {
        if (onLogoutSuccessListener != null) {
            onLogoutSuccessListener.onLogoutSuccess();
        }
        snappyRepository.clearAll();
        appSessionHolder.destroy();
        notificationDelegate.cancelAll();
        badgeUpdater.updateBadge(0);
        FlowManager.getDatabase(MessengerDatabase.NAME).reset(context);
    }

    public void setOnLogoutSuccessListener(OnLogoutSuccessListener onLogoutSuccessListener) {
        this.onLogoutSuccessListener = onLogoutSuccessListener;
    }

    public interface OnLogoutSuccessListener {
        void onLogoutSuccess();
    }
}
