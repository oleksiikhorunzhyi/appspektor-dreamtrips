package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import org.immutables.value.Value;

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
public class FetchCardPropertiesCommand extends Command<FetchCardPropertiesCommand.Properties> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;

   @Override
   protected void run(CommandCallback<Properties> callback) throws Throwable {
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
                  (Properties) ImmutableProperties.builder()
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

   @Value.Immutable
   public interface Properties {

      String sdkVersion();

      String firmWareVersion();

      int batteryLevel();

      boolean lock();

      boolean stealthMode();

      long disableCardDelay();

      long clearFlyeDelay();
   }
}