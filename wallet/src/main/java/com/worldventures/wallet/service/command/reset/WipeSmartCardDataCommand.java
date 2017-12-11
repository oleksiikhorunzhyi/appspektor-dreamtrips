package com.worldventures.wallet.service.command.reset;

import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.DisassociateCardUserHttpAction;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.entity.SmartCard;
import com.worldventures.wallet.domain.entity.SmartCardDetails;
import com.worldventures.wallet.service.SmartCardLocationInteractor;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.service.lostcard.command.UpdateTrackingStatusCommand;

import java.net.HttpURLConnection;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.JanetActionException;
import io.techery.janet.smartcard.action.user.UnAssignUserAction;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class WipeSmartCardDataCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet walletJanet;
   @Inject Janet apiLibJanet;
   @Inject SmartCardLocationInteractor smartCardLocationInteractor;

   private final ResetOptions factoryResetOptions;

   public WipeSmartCardDataCommand() {
      this.factoryResetOptions = ResetOptions.builder()
            .wipePaymentCards(true)
            .wipeUserSmartCardData(true)
            .build();
   }

   public WipeSmartCardDataCommand(ResetOptions factoryResetOptions) {
      this.factoryResetOptions = factoryResetOptions;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      reset().subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> reset() {
      return fetchSmartCard()
            .flatMap(this::disassociateCardUserServer)
            .flatMap(action -> clearSmartCardSettings())
            .flatMap(action -> disassociateCardUser())
            .flatMap(action -> removeSmartCardData())
            .map(action -> null);
   }

   private Observable<SmartCard> fetchSmartCard() {
      return walletJanet.createPipe(ActiveSmartCardCommand.class)
            .createObservableResult(new ActiveSmartCardCommand())
            .map(Command::getResult);
   }

   private Observable<Void> disassociateCardUserServer(SmartCard smartCard) {
      final SmartCardDetails details = smartCard.getDetails();
      return apiLibJanet.createPipe(DisassociateCardUserHttpAction.class, Schedulers.io())
            .createObservableResult(
                  new DisassociateCardUserHttpAction(Long.parseLong(smartCard.getSmartCardId()),
                        details != null ? details.getDeviceId() : null))
            .map(disassociateCardUserHttpAction -> (Void) null)
            .onErrorResumeNext(this::handleDisassociateError);
   }

   private Observable<Void> handleDisassociateError(Throwable throwable) {
      JanetActionException actionException = (JanetActionException) throwable;
      if (((BaseHttpAction) actionException.getAction()).statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
         return Observable.just(null);
      } else {
         return Observable.error(throwable);
      }
   }

   private Observable<UnAssignUserAction> disassociateCardUser() {
      return walletJanet.createPipe(DeviceStateCommand.class)
            .createObservableResult(DeviceStateCommand.Companion.fetch())
            .map(command -> command.getResult().getConnectionStatus().isConnected())
            .flatMap(connected -> connected ? walletJanet.createPipe(UnAssignUserAction.class)
                  .createObservableResult(new UnAssignUserAction()) : Observable.just(null));
   }

   private Observable<Void> clearSmartCardSettings() {
      return smartCardLocationInteractor.updateTrackingStatusPipe()
            .createObservableResult(new UpdateTrackingStatusCommand(false))
            .map(disassociateCardUserHttpAction -> (Void) null);
   }

   private Observable<RemoveSmartCardDataCommand> removeSmartCardData() {
      return walletJanet.createPipe(RemoveSmartCardDataCommand.class)
            .createObservableResult(new RemoveSmartCardDataCommand(factoryResetOptions));
   }
}
