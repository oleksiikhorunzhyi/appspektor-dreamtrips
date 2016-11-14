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
import io.techery.janet.smartcard.action.records.GetClearRecordsDelayAction;
import io.techery.janet.smartcard.action.settings.GetDisableDefaultCardDelayAction;
import io.techery.janet.smartcard.action.settings.GetStealthModeAction;
import io.techery.janet.smartcard.action.support.GetBatteryLevelAction;
import io.techery.janet.smartcard.action.support.GetFirmwareVersionAction;
import io.techery.janet.smartcard.action.support.GetSDKVersionAction;
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
      Observable.zip(
            janet.createPipe(GetSDKVersionAction.class)
                  .createObservableResult(new GetSDKVersionAction()),
            janet.createPipe(GetFirmwareVersionAction.class)
                  .createObservableResult(new GetFirmwareVersionAction()),
            janet.createPipe(GetBatteryLevelAction.class)
                  .createObservableResult(new GetBatteryLevelAction()),
            janet.createPipe(GetLockDeviceStatusAction.class)
                  .createObservableResult(new GetLockDeviceStatusAction()),
            janet.createPipe(GetStealthModeAction.class)
                  .createObservableResult(new GetStealthModeAction()),
            janet.createPipe(GetDisableDefaultCardDelayAction.class)
                  .createObservableResult(new GetDisableDefaultCardDelayAction()),
            janet.createPipe(GetClearRecordsDelayAction.class)
                  .createObservableResult(new GetClearRecordsDelayAction()),
            (sdkVersionAction, firmwareVersionAction, getBatteryLevelAction, getLockDeviceStatusAction,
                  getStealthModeAction, getDisableDefaultCardDelayAction, getClearRecordsDelayAction) ->
                  ImmutableSmartCard.builder().from(smartCard)
                        .sdkVersion(sdkVersionAction.version)
                        .firmWareVersion(firmwareVersionAction.version)
                        .batteryLevel(Integer.parseInt(getBatteryLevelAction.level))
                        .lock(getLockDeviceStatusAction.locked)
                        .stealthMode(getStealthModeAction.enabled)
                        .disableCardDelay(getDisableDefaultCardDelayAction.delay)
                        .clearFlyeDelay(getClearRecordsDelayAction.delay)
                        .build())
            .subscribe(callback::onSuccess, callback::onFail);
   }
}