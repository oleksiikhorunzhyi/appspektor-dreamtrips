package com.worldventures.wallet.service.command.reset;

import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.DisassociateCardUserHttpAction;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.entity.SmartCard;
import com.worldventures.wallet.domain.entity.SmartCardDetails;
import com.worldventures.wallet.domain.entity.SmartCardStatus;
import com.worldventures.wallet.service.SmartCardLocationInteractor;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.util.SmartCardConnectException;
import com.worldventures.wallet.util.WalletFeatureHelper;

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
   @Inject WalletFeatureHelper featureHelper;

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

   private boolean isSmartCardAvailable() {
      return factoryResetOptions.isSmartCardIsAvailable();
   }

   /**
    * Step 0 we should check connection with smart-card
    * Step 1 we disassociate card on server side
    * Step 2 we wipe smart-card data
    * Step 3 we wipe settings
    * Step 4 we clear cache
    *
    * @return Observable which'll emmit event after operation finish
    */
   private Observable<Void> reset() {
      return fetchWipeData()
            .flatMap(wipeData -> {
               if (!isSmartCardAvailable() || wipeData.smartCardStatus.getConnectionStatus().isConnected()) {
                  return disassociateCardUserServer(wipeData.smartCard)
                        .flatMap(aVoid -> disassociateCardUser())
                        .flatMap(action -> clearSmartCardSettings())
                        .flatMap(aVoid -> removeSmartCardData());
               } else {
                  return Observable.error(new SmartCardConnectException("Factory resent cannot be finished"));
               }
            });
   }

   private Observable<WipeData> fetchWipeData() {
      return Observable.zip(
            walletJanet.createPipe(ActiveSmartCardCommand.class)
                  .createObservableResult(new ActiveSmartCardCommand()),
            walletJanet.createPipe(DeviceStateCommand.class)
                  .createObservableResult(DeviceStateCommand.Companion.fetch()),
            (smartCardCommand, smartCardStateCommand) ->
                  new WipeData(smartCardCommand.getResult(), smartCardStateCommand.getResult())
      );
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

   /**
    * Result will be emitted on Android Main Thread
    */
   private Observable<Void> disassociateCardUser() {
      if (isSmartCardAvailable()) {
         return walletJanet.createPipe(UnAssignUserAction.class)
               .createObservableResult(new UnAssignUserAction())
               .map(action -> null);
      }
      return Observable.just(null);
   }

   private Observable<Void> clearSmartCardSettings() {
      return featureHelper.clearSettings(smartCardLocationInteractor);
   }

   private Observable<Void> removeSmartCardData() {
      return walletJanet.createPipe(RemoveSmartCardDataCommand.class)
            .createObservableResult(new RemoveSmartCardDataCommand(factoryResetOptions))
            .map(action -> (Void) null);
   }

   private static final class WipeData {

      private final SmartCard smartCard;
      private final SmartCardStatus smartCardStatus;

      private WipeData(SmartCard smartCard, SmartCardStatus smartCardStatus) {
         this.smartCard = smartCard;
         this.smartCardStatus = smartCardStatus;
      }
   }
}
