package com.worldventures.dreamtrips.wallet.service.lostcard.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.settings.command.SettingsCommand;
import com.worldventures.dreamtrips.modules.settings.model.FlagSetting;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.service.SettingsInteractor;
import com.worldventures.dreamtrips.wallet.di.external.WalletTrackingStatusStorage;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class UpdateTrackingStatusCommand extends Command<Void> implements InjectableAction {

   @Inject SettingsInteractor settingsInteractor;
   @Inject LostCardRepository lostCardRepository;

   private final boolean enabled;

   public UpdateTrackingStatusCommand(boolean enabled) {
      this.enabled = enabled;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      settingsInteractor.settingsActionPipe()
            .createObservableResult(new SettingsCommand(prepareTrackingStatus()))
            .onErrorReturn(throwable -> null)
            .flatMap(settingsCommand -> saveTrackingStatus())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> saveTrackingStatus() {
      lostCardRepository.saveEnabledTracking(enabled);
      return Observable.just(null);
   }

   private List<Setting> prepareTrackingStatus() {
      final FlagSetting trackingStatusSetting =
            new FlagSetting(WalletTrackingStatusStorage.SETTING_TRACKING_STATUS, Setting.Type.FLAG, enabled);
      return Collections.singletonList(trackingStatusSetting);
   }
}
