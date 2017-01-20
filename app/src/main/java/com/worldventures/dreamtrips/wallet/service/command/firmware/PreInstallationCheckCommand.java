package com.worldventures.dreamtrips.wallet.service.command.firmware;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareRepository;

import org.immutables.value.Value;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.charger.CardInChargerAction;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.action.support.GetBatteryLevelAction;
import io.techery.janet.smartcard.model.ConnectionType;
import io.techery.janet.smartcard.model.ImmutableConnectionParams;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils.SUPPORTED_CHARGER_ACTION_VERSION_FW;
import static com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils.firmwareStringToInt;
import static com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils.isNewFirmwareAvailableForCharger;
import static java.lang.Integer.parseInt;

@CommandAction
public class PreInstallationCheckCommand extends Command<PreInstallationCheckCommand.Checks> implements InjectableAction {

   private final static int MIN_BATTERY_LEVEL = 50;

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject WalletBluetoothService bluetoothService;
   @Inject FirmwareRepository firmwareRepository;

   @Override
   protected void run(CommandCallback<PreInstallationCheckCommand.Checks> callback) throws Throwable {
      final FirmwareUpdateData firmwareUpdateData = firmwareRepository.getFirmwareUpdateData();
      final SmartCardFirmware smartCardFirmware = firmwareUpdateData.currentFirmwareVersion();

      Observable.zip(
            janet.createPipe(ConnectAction.class).createObservableResult(new ConnectAction(ImmutableConnectionParams
                  .of(parseInt(firmwareRepository.getFirmwareUpdateData().smartCardId())))).map(action -> action.type),
            cardInChargerObservable(smartCardFirmware),
            janet.createPipe(GetBatteryLevelAction.class).createObservableResult(
                  new GetBatteryLevelAction()).map(action -> parseInt(action.level)),
            (connectionType, inCharger, batteryLevel) -> check(connectionType, inCharger, batteryLevel, firmwareUpdateData)
      ).subscribe(callback::onSuccess, callback::onFail);
   }

   private Checks check(ConnectionType connectionType, boolean inCharger, int batteryLevel, FirmwareUpdateData firmwareUpdateData) {
      boolean bluetoothEnabled = bluetoothService.isEnable();
      boolean smartCardConnected = bluetoothEnabled &&
            (connectionType == ConnectionType.APP || connectionType == ConnectionType.DFU);
      boolean smartCardCharged = smartCardConnected && batteryLevel >= MIN_BATTERY_LEVEL;
      boolean smartCardOnDeviceRequired = isNewFirmwareAvailableForCharger(
            firmwareUpdateData.currentFirmwareVersion().externalAtmelVersion(),
            firmwareUpdateData.firmwareInfo().firmwareVersions().puckAtmelVerstion());

      return ImmutableChecks.builder()
            .bluetoothIsEnabled(bluetoothEnabled)
            .smartCardIsCharged(smartCardCharged)
            .smartCardIsConnected(smartCardConnected)
            .connectCardToChargerRequired(smartCardOnDeviceRequired)
            .connectToCharger(inCharger)
            .build();
   }

   private Observable<Boolean> cardInChargerObservable(SmartCardFirmware smartCardFirmware) {
      // TODO: 1/10/17 check for v37, in charger, it is not support
      if (firmwareStringToInt(smartCardFirmware.nordicAppVersion()) >= SUPPORTED_CHARGER_ACTION_VERSION_FW) {
         return janet.createPipe(CardInChargerAction.class)
               .createObservableResult(new CardInChargerAction())
               .map(action -> action.inCharger);
      } else {
         return Observable.just(true);
      }
   }

   @Value.Immutable
   public interface Checks {

      boolean bluetoothIsEnabled();

      boolean smartCardIsConnected();

      boolean smartCardIsCharged();

      boolean connectCardToChargerRequired();

      boolean connectToCharger();
   }
}
