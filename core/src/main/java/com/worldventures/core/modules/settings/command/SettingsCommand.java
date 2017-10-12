package com.worldventures.core.modules.settings.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.R;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.modules.settings.model.Setting;
import com.worldventures.dreamtrips.api.settings.UpdateSettingsHttpAction;
import com.worldventures.dreamtrips.api.settings.model.ImmutableFlagSetting;
import com.worldventures.dreamtrips.api.settings.model.ImmutableSelectSetting;
import com.worldventures.dreamtrips.api.settings.model.ImmutableSettingsBody;
import com.worldventures.dreamtrips.api.settings.model.ImmutableUnknownSetting;
import com.worldventures.dreamtrips.api.settings.model.SettingsBody;
import com.worldventures.core.janet.CommandWithError;

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
            .createObservableResult(new UpdateSettingsHttpAction(convert(settings)))
            .subscribe(updateSettingsHttpAction -> callback.onSuccess(null), callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_update_settings;
   }

   private SettingsBody convert(List<Setting> settings) {
      ImmutableSettingsBody.Builder settingsBodyBuilder = ImmutableSettingsBody.builder();
      Queryable.from(settings).forEachR(setting -> {
         switch (setting.getType()) {
            case FLAG:
               ImmutableFlagSetting flagSetting = ImmutableFlagSetting.builder()
                     .name(setting.getName())
                     .value((Boolean) setting.getValue())
                     .build();
               settingsBodyBuilder.addSettings(flagSetting);
               break;
            case SELECT:
               ImmutableSelectSetting selectSetting = ImmutableSelectSetting.builder()
                     .name(setting.getName())
                     .value((String) setting.getValue())
                     .addOptions("") //client need it, server - not
                     .build();
               settingsBodyBuilder.addSettings(selectSetting);
               break;
            case UNKNOWN:
               ImmutableUnknownSetting unknownSetting = ImmutableUnknownSetting.builder()
                     .name(setting.getName())
                     .value((String) setting.getValue())
                     .build();
               settingsBodyBuilder.addSettings(unknownSetting);
               break;
            default:
               break;
         }
      });
      return settingsBodyBuilder.build();
   }
}
