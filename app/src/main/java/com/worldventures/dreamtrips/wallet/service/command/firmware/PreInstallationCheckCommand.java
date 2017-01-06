package com.worldventures.dreamtrips.wallet.service.command.firmware;

import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;

import org.immutables.value.Value;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.charger.CardInChargerAction;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils.newFirmwareAvailable;

@CommandAction
public class PreInstallationCheckCommand extends Command<PreInstallationCheckCommand.Checks> implements InjectableAction {

   private final static int MIN_BATTERY_LEVEL = 50;

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject WalletBluetoothService bluetoothService;
   @Inject SmartCardInteractor smartCardInteractor;

   private final FirmwareInfo firmwareInfo;

   public PreInstallationCheckCommand(FirmwareInfo firmwareInfo) {
      this.firmwareInfo = firmwareInfo;
   }

   @Override
   protected void run(CommandCallback<PreInstallationCheckCommand.Checks> callback) throws Throwable {
      Observable.combineLatest(
            smartCardInteractor.activeSmartCardPipe().createObservableResult(new ActiveSmartCardCommand()),
            janet.createPipe(CardInChargerAction.class).createObservableResult(new CardInChargerAction()),
            (smartCardCommand, cardInChargerCommand) -> new Pair<>(smartCardCommand.getResult(), cardInChargerCommand.inCharger))
            .map(dataPair -> check(dataPair.first, dataPair.second))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Checks check(SmartCard smartCard, boolean inCharger) {
      boolean bluetoothEnabled = bluetoothService.isEnable();
      boolean smartCardConnected = bluetoothEnabled &&
            (smartCard.connectionStatus() == SmartCard.ConnectionStatus.CONNECTED || smartCard.connectionStatus() == SmartCard.ConnectionStatus.DFU);
      boolean smartCardCharged = smartCardConnected && smartCard.batteryLevel() >= MIN_BATTERY_LEVEL;
      boolean smartCardOnDeviceRequired = newFirmwareAvailable(smartCard.firmwareVersion().externalAtmelVersion(),
            firmwareInfo.firmwareVersions().puckAtmelVerstion());
      return ImmutableChecks.builder()
            .bluetoothIsEnabled(bluetoothEnabled)
            .smartCardIsCharged(smartCardCharged)
            .smartCardIsConnected(smartCardConnected)
            .connectCardToChargerRequired(smartCardOnDeviceRequired)
            .connectToCharger(inCharger)
            .build();
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
