package com.worldventures.dreamtrips.wallet.service.command;

import android.util.Pair;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.util.CardUtils;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SaveCardDetailsDataCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SnappyRepository snappyRepository;
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

      if (setAsDefaultAddress && !useDefaultAddress) {
         snappyRepository.saveDefaultAddress(manualAddressInfo);
      }

      Observable<ActionState<GetDefaultAddressCommand>> defaultAddressInfoStateObservable = smartCardInteractor.getDefaultAddressCommandPipe()
            .observeWithReplay()
            .takeFirst(state -> state.status == ActionState.Status.SUCCESS || state.status == ActionState.Status.FAIL);
      Observable<ActionState<FetchDefaultCardCommand>> defaultCardStateObservable = smartCardInteractor.fetchDefaultCardCommandActionPipe()
            .observeWithReplay()
            .takeFirst(state -> state.status == ActionState.Status.SUCCESS || state.status == ActionState.Status.FAIL);

      Observable.combineLatest(defaultCardStateObservable, defaultAddressInfoStateObservable, (defaultCardState, addressInfoState) -> {
         AddressInfo address = useDefaultAddress ? addressInfoState.action.getResult() : manualAddressInfo;
         BankCard extandedBankCard = ImmutableBankCard.copyOf(bankCard)
               .withCvv(Integer.parseInt(cvv))
               .withTitle(nickName)
               .withAddressInfo(address);
         return new Pair<>(extandedBankCard, !CardUtils.isRealCardId(defaultCardState.action.getResult()));
      })
            .flatMap(bankCardPair -> smartCardInteractor.addRecordPipe()
                  .createObservableResult(new AttachCardCommand(bankCardPair.first, bankCardPair.second)))
            .subscribe(attachCardCommand -> callback.onSuccess(null));
   }

   private void checkCardAddress() throws FormatException {
      boolean validInfo = (useDefaultAddress || WalletValidateHelper.validateAddressInfo(manualAddressInfo)) && WalletValidateHelper
            .validateCardCvv(cvv);

      if (!validInfo) throw new FormatException();
   }

}
