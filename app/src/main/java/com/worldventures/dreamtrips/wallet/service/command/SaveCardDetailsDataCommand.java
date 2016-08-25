package com.worldventures.dreamtrips.wallet.service.command;

import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.util.CardUtils;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SaveCardDetailsDataCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;

   private final BankCard bankCard;
   private final AddressInfo manualAddressInfo;
   private final String cvv;
   private final String nickName;
   private final boolean setAsDefaultAddress;
   private final boolean useDefaultAddress;

   public SaveCardDetailsDataCommand(BankCard bankCard, AddressInfo manualAddressInfo, String nickName, String cvv, boolean useDefaultAddress, boolean setAsDefaultAddress) {
      this.setAsDefaultAddress = setAsDefaultAddress;
      this.useDefaultAddress = useDefaultAddress;
      this.nickName = nickName;
      this.cvv = cvv;
      this.bankCard = bankCard;
      this.manualAddressInfo = manualAddressInfo;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      checkCardAddress();

      Observable.just(setAsDefaultAddress && !useDefaultAddress)
            .flatMap(this::saveDefaultAddressObservable)
            .flatMap(saveDefaultAddressCommand -> fetchDefaultCardAndAddressObservable())
            .flatMap(bankCardPair -> smartCardInteractor.addRecordPipe().createObservableResult(new AttachCardCommand(bankCardPair.first, bankCardPair.second)))
            .subscribe(attachCardCommand -> callback.onSuccess(null));
   }

   private Observable<SaveDefaultAddressCommand> saveDefaultAddressObservable (boolean saveDefaultAddress){
         return !setAsDefaultAddress ? Observable.just(null) :
            smartCardInteractor.saveDefaultAddressPipe().createObservableResult(new SaveDefaultAddressCommand(manualAddressInfo));
   }

   private Observable<Pair<BankCard, Boolean>> fetchDefaultCardAndAddressObservable() {
      return Observable.zip(
            smartCardInteractor.fetchDefaultCardCommandActionPipe().createObservableResult(FetchDefaultCardCommand.fetch(false)),
            smartCardInteractor.getDefaultAddressCommandPipe().createObservableResult(new GetDefaultAddressCommand()),
            (defaultCardAction, addressInfoAction) -> {
               AddressInfo address = useDefaultAddress ? addressInfoAction.getResult() : manualAddressInfo;
               BankCard extandedBankCard = ImmutableBankCard.copyOf(bankCard)
                     .withCvv(Integer.parseInt(cvv))
                     .withTitle(nickName)
                     .withAddressInfo(address);
               return new Pair<>(extandedBankCard, !CardUtils.isRealCardId(defaultCardAction.getResult()));
            });
   }

   private void checkCardAddress() throws FormatException {
      boolean validInfo = (useDefaultAddress || WalletValidateHelper.validateAddressInfo(manualAddressInfo)) && WalletValidateHelper
            .validateCardCvv(cvv);

      if (!validInfo) throw new FormatException();
   }

}
