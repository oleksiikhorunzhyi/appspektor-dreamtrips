package com.worldventures.dreamtrips.modules.settings.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.settings.bundle.api.UpdateSettingsHttpAction;
import com.worldventures.dreamtrips.modules.settings.model.Setting;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class SettingsCommand extends CommandWithError<Void> implements InjectableAction {

    @Inject Janet janet;

    private List<Setting> settings;

    public SettingsCommand(List<Setting> settings) {
        this.settings = settings;
    }

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        janet.createPipe(UpdateSettingsHttpAction.class, Schedulers.io())
                .createObservableResult(new UpdateSettingsHttpAction(settings))
                .subscribe(updateSettingsHttpAction -> callback.onSuccess(null), callback::onFail);
    }

    @Override
    public int getFallbackErrorMessage() {
        return R.string.error_fail_to_update_settings;
    }
}
