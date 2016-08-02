package com.worldventures.dreamtrips.modules.common.api.janet.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.api.janet.GetAppSettingsHttpAction;
import com.worldventures.dreamtrips.modules.settings.model.SettingsHolder;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;
import com.worldventures.dreamtrips.modules.settings.util.SettingsManager;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class AppSettingsCommand extends CommandWithError<SettingsHolder> implements InjectableAction {

    @Inject Janet janet;
    @Inject SnappyRepository db;

    @Override
    protected void run(CommandCallback<SettingsHolder> callback) throws Throwable {
        janet.createPipe(GetAppSettingsHttpAction.class, Schedulers.io())
                .createObservableResult(new GetAppSettingsHttpAction())
                .map(GetAppSettingsHttpAction::getSettingsHolder)
                .doOnNext(action -> {
                    db.saveSettings(SettingsManager.merge(action.getSettings(),
                            SettingsFactory.createSettings()), true);
                }).subscribe(callback::onSuccess, callback::onFail);
    }

    @Override
    public int getFallbackErrorMessage() {
        return R.string.error_fail_to_load_settings;
    }
}
