package com.worldventures.dreamtrips.modules.common.view.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.DeleteTokenGcmTask;
import com.worldventures.dreamtrips.modules.common.api.LogoutCommand;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;
import com.worldventures.dreamtrips.modules.feed.api.UnsubscribeDeviceCommand;

import javax.inject.Inject;

public class LogoutDelegate {

    @Inject
    protected Context context;
    @Inject
    protected SnappyRepository snappyRepository;
    @Inject
    protected SessionHolder<UserSession> appSessionHolder;
    @Inject
    protected ActivityRouter activityRouter;

    private RequestingPresenter presenter;
    private OnLogoutSuccessListener onLogoutSuccessListener;

    public LogoutDelegate(RequestingPresenter presenter) {
        this.presenter = presenter;
    }

    public void logout() {
        String token = snappyRepository.getGcmRegToken();
        if (token != null) {
            presenter.doRequest(new UnsubscribeDeviceCommand(token), aVoid -> deleteTokenInGcm());
        } else {
            deleteSession();
        }
    }

    private void deleteTokenInGcm (){
        new DeleteTokenGcmTask(context, (task, removeGcmTokenSucceed) -> {
            deleteSession();
        }).execute();
    }

    private void deleteSession() {
        presenter.doRequest(new LogoutCommand(), aVoid -> clearUserDataAndFinish());
    }

    private void clearUserDataAndFinish(){
        if (onLogoutSuccessListener != null) {
            onLogoutSuccessListener.onLogoutSuccess();
        }
        snappyRepository.clearAll();
        appSessionHolder.destroy();
        activityRouter.finish(); // for sure if activity start finishing
        moveToLogin();
    }

    private void moveToLogin() {
        Bundle args = new Bundle();
        args.putSerializable(ComponentPresenter.COMPONENT_TOOLBAR_CONFIG, ToolbarConfig.Builder.create().visible(false).build());
        activityRouter.openComponentActivity(Route.LOGIN, args,
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    public void setOnLogoutSuccessListener(OnLogoutSuccessListener onLogoutSuccessListener) {
        this.onLogoutSuccessListener = onLogoutSuccessListener;
    }

    public interface OnLogoutSuccessListener {
        void onLogoutSuccess();
    }
}
