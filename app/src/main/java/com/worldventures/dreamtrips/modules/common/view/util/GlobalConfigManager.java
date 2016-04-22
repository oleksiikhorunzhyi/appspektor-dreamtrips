package com.worldventures.dreamtrips.modules.common.view.util;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.events.AppConfigUpdatedEvent;
import com.worldventures.dreamtrips.modules.common.api.GlobalConfigQuery;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.model.ServerStatus;

import de.greenrobot.event.EventBus;

public class GlobalConfigManager {

    protected SessionHolder<UserSession> appSessionHolder;
    protected EventBus eventBus;

    public GlobalConfigManager(SessionHolder<UserSession> appSessionHolder, EventBus eventBus) {
        this.appSessionHolder = appSessionHolder;
        this.eventBus = eventBus;
    }

    public void loadGlobalConfig(DreamSpiceManager dreamSpiceManager, SuccessConfigListener successConfigListener,
                                 ErrorConfigListener errorConfigListener) {
        GlobalConfigQuery.GetConfigRequest getConfigRequest = new GlobalConfigQuery.GetConfigRequest();
        dreamSpiceManager.execute(getConfigRequest,
                appConfig -> processAppConfig(appConfig, successConfigListener, errorConfigListener),
                error -> errorConfigListener.onConfigError());
    }

    private void processAppConfig(AppConfig appConfig, SuccessConfigListener successConfigListener,
                                   ErrorConfigListener errorConfigListener) {
        ServerStatus.Status serv = appConfig.getServerStatus().getProduction();
        String status = serv.getStatus();

        if (!"up".equalsIgnoreCase(status)) {
            errorConfigListener.onConfigError();
        } else {
            UserSession userSession;
            if (appSessionHolder.get().isPresent()) {
                userSession = appSessionHolder.get().get();
            } else {
                userSession = new UserSession();
            }

            userSession.setGlobalConfig(appConfig);
            appSessionHolder.put(userSession);
            eventBus.postSticky(new AppConfigUpdatedEvent());
            successConfigListener.onConfigSuccess();
        }
    }

    public interface SuccessConfigListener {

        void onConfigSuccess();
    }

    public interface ErrorConfigListener {

        void onConfigError();
    }
}
