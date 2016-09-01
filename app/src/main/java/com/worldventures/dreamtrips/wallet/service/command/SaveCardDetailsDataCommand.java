package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class SaveCardDetailsDataCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;

   private final BankCard bankCard;
   private final AddressInfo manualAddressInfo;
   private final String cvv;
   private final String nickName;
   private final boolean setAsDefaultAddress;
   private final boolean useDefaultAddress;
   private final boolean setAsDefaultCard;

   public SaveCardDetailsDataCommand(BankCard bankCard, AddressInfo manualAddressInfo, String nickName, String cvv, boolean useDefaultAddress, boolean setAsDefaultAddress, boolean setAsDefaultCard) {
      this.setAsDefaultAddress = setAsDefaultAddress;
      this.useDefaultAddress = useDefaultAddress;
      this.nickName = nickName;
      this.cvv = cvv;
      this.bankCard = bankCard;
      this.manualAddressInfo = manualAddressInfo;
      this.setAsDefaultCard = setAsDefaultCard;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      checkCardAddress();

      Observable.just(setAsDefaultAddress && !useDefaultAddress)
            .flatMap(this::saveDefaultAddressObservable)
            .flatMap(saveDefaultAddressCommand -> fetchCardWithAddressObservable())
            .flatMap(cardWithAddress -> smartCardInteractor.addRecordPipe()
                  .createObservableResult(new AttachCardCommand(cardWithAddress, setAsDefaultCard)))
            .subscribe(attachCardCommand -> callback.onSuccess(null));
   }

   private Observable<SaveDefaultAddressCommand> saveDefaultAddressObservable(boolean saveDefaultAddress) {
      return !setAsDefaultAddress ? Observable.just(null) :
            smartCardInteractor.saveDefaultAddressPipe()
                  .createObservableResult(new SaveDefaultAddressCommand(manualAddressInfo));
   }

   private Observable<BankCard> fetchCardWithAddressObservable() {
      return smartCardInteractor.getDefaultAddressCommandPipe().createObservableResult(new GetDefaultAddressCommand())
            .map(addressInfoAction -> {
               AddressInfo address = useDefaultAddress ? addressInfoAction.getResult() : manualAddressInfo;
               return ImmutableBankCard.copyOf(bankCard)
                     .withCvv(Integer.parseInt(cvv))
                     .withTitle(nickName)
                     .withAddressInfo(address);
            });
   }

   private void checkCardAddress() throws FormatException {
      boolean validInfo = (useDefaultAddress || WalletValidateHelper.validateAddressInfo(manualAddressInfo)) && WalletValidateHelper
            .validateCardCvv(cvv);

      if (!validInfo) throw new FormatException();
   }

}
