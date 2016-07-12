package com.worldventures.dreamtrips.modules.common.view.util;

import android.content.Context;

import com.messenger.delegate.FlagsDelegate;
import com.messenger.storage.MessengerDatabase;
import com.messenger.synchmechanism.MessengerConnector;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.preference.LocalesHolder;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.BadgeUpdater;
import com.worldventures.dreamtrips.core.utils.DTCookieManager;
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
    @Inject
    protected DTCookieManager cookieManager;
    @Inject
    FlagsDelegate flagsDelegate;
    @Inject
    LocalesHolder localesHolder;
    @Inject
    StaticPageHolder staticPageHolder;
    @Inject
    MessengerConnector messengerConnector;
    protected DreamSpiceManager dreamSpiceManager;

    private OnLogoutSuccessListener onLogoutSuccessListener;

    public LogoutDelegate(Injector injector) {
        injector.inject(this);
    }

    public void setDreamSpiceManager(DreamSpiceManager dreamSpiceManager) {
        this.dreamSpiceManager = dreamSpiceManager;
    }

    public void logout() {
        messengerConnector.disconnect();
        flagsDelegate.clearCache();
        String token = snappyRepository.getGcmRegToken();
        if (token != null) {
            if (!dreamSpiceManager.isStarted()) dreamSpiceManager.start(context);
            //
            dreamSpiceManager.clearExecute(new UnsubscribeDeviceCommand(token),
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
        cookieManager.clearCookies();
        snappyRepository.clearAll();
        appSessionHolder.destroy();
        staticPageHolder.destroy();
        localesHolder.destroy();
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
