package com.worldventures.dreamtrips.modules.common.delegate;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.events.AppConfigUpdatedEvent;
import com.worldventures.dreamtrips.modules.common.api.janet.GlobalConfigAction;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.model.ServerStatus;

import de.greenrobot.event.EventBus;
import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class GlobalConfigManager {

    protected SessionHolder<UserSession> appSessionHolder;
    protected EventBus eventBus;
    protected ActionPipe<GlobalConfigAction> configPipe;

    public GlobalConfigManager(SessionHolder<UserSession> appSessionHolder, Janet janet, EventBus eventBus) {
        this.appSessionHolder = appSessionHolder;
        this.configPipe = janet.createPipe(GlobalConfigAction.class, Schedulers.io());
        this.eventBus = eventBus;
    }

    public void loadGlobalConfig(SuccessConfigListener successConfigListener,
                                 ErrorConfigListener errorConfigListener) {
        configPipe.createObservableResult(new GlobalConfigAction())
                .take(1)
                .subscribe(action -> processAppConfig(action.getAppConfig(), successConfigListener,
                        errorConfigListener),
                        e -> errorConfigListener.onConfigError());
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
