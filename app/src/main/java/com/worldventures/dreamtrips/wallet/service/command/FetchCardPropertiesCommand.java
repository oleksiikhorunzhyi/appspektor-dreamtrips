package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.lock.GetLockDeviceStatusAction;
import io.techery.janet.smartcard.action.settings.GetStealthModeAction;
import io.techery.janet.smartcard.action.support.GetBatteryLevelAction;
import rx.Observable;

@CommandAction
public class FetchCardPropertiesCommand extends Command<SmartCard> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;

   private final SmartCard smartCard;

   public FetchCardPropertiesCommand(SmartCard smartCard) {
      this.smartCard = smartCard;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      getBatteryLevel(ImmutableSmartCard.builder().from(smartCard)).flatMap(this::getLockStatus)
            .flatMap(this::getStealthMode)
            .map(ImmutableSmartCard.Builder::build)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<ImmutableSmartCard.Builder> getBatteryLevel(ImmutableSmartCard.Builder smartCardBuilder) {
      return janet.createPipe(GetBatteryLevelAction.class)
            .createObservableResult(new GetBatteryLevelAction())
            .map(action -> smartCardBuilder.batteryLevel(Integer.parseInt(action.level)));
   }

   private Observable<ImmutableSmartCard.Builder> getLockStatus(ImmutableSmartCard.Builder smartCardBuilder) {
      return janet.createPipe(GetLockDeviceStatusAction.class)
            .createObservableResult(new GetLockDeviceStatusAction())
            .map(action -> smartCardBuilder.lock(action.locked));
   }

   private Observable<ImmutableSmartCard.Builder> getStealthMode(ImmutableSmartCard.Builder smartCardBuilder) {
      return janet.createPipe(GetStealthModeAction.class)
            .createObservableResult(new GetStealthModeAction())
            .map(action -> smartCardBuilder.stealthMode(action.enabled));
   }
}
