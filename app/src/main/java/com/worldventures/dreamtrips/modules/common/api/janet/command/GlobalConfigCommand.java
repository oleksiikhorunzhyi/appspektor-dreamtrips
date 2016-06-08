package com.worldventures.dreamtrips.modules.common.api.janet.command;

import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.events.AppConfigUpdatedEvent;
import com.worldventures.dreamtrips.modules.common.api.janet.GetGlobalConfigurationHttpAction;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.model.ServerStatus;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GlobalConfigCommand extends Command<AppConfig> implements InjectableAction {

    @Inject
    Janet janet;
    @Inject
    SessionHolder<UserSession> appSessionHolder;
    @Global
    @Inject
    EventBus eventBus;

    @Override
    protected void run(CommandCallback<AppConfig> callback) throws Throwable {
        janet.createPipe(GetGlobalConfigurationHttpAction.class)
                .createObservableResult(new GetGlobalConfigurationHttpAction())
                .map(GetGlobalConfigurationHttpAction::getAppConfig)
                .doOnNext(this::processAppConfig)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    private void processAppConfig(AppConfig appConfig) {
        ServerStatus.Status serv = appConfig.getServerStatus().getProduction();
        String status = serv.getStatus();

        if (!"up".equalsIgnoreCase(status)) {
            throw new IllegalStateException("Server Status is " + status + ". User can't be logged in");
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
        }
    }
}
