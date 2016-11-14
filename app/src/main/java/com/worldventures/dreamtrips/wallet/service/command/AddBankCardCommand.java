package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

@CommandAction
public class AddBankCardCommand extends Command<BankCard> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject SnappyRepository snappyRepository;

   private final BankCard bankCard;
   private final AddressInfo manualAddressInfo;
   private final String cvv;
   private final String nickName;
   private final RecordIssuerInfo issuerInfo;
   private final boolean setAsDefaultAddress;
   private final boolean useDefaultAddress;
   private final boolean setAsDefaultCard;

   private AddBankCardCommand(BankCard bankCard,
         AddressInfo manualAddressInfo,
         String nickName,
         String cvv,
         RecordIssuerInfo issuerInfo,
         boolean useDefaultAddress,
         boolean setAsDefaultAddress,
         boolean setAsDefaultCard
   ) {
      this.issuerInfo = issuerInfo;
      this.setAsDefaultAddress = setAsDefaultAddress;
      this.useDefaultAddress = useDefaultAddress;
      this.nickName = nickName;
      this.cvv = cvv;
      this.bankCard = bankCard;
      this.manualAddressInfo = manualAddressInfo;
      this.setAsDefaultCard = setAsDefaultCard;
   }

   @Override
   protected void run(CommandCallback<BankCard> callback) throws Throwable {
      checkCardData();

      createCardWithAddress()
            .flatMap(this::pushBankCard)
            .subscribe(bankCard -> {
               saveDefaultAddressIfNeed();
               Timber.d("Card was added successfully");
               callback.onSuccess(bankCard);
            }, throwable -> {
               Timber.e(throwable, "Card was not added");
               callback.onFail(throwable);
            });
   }

   public boolean setAsDefaultCard() {
      return setAsDefaultCard;
   }

   private void saveDefaultAddressIfNeed() {
      if (setAsDefaultAddress) {
         snappyRepository.saveDefaultAddress(manualAddressInfo);
      }
   }

   private Observable<BankCard> createCardWithAddress() {
      if (useDefaultAddress) {
         return smartCardInteractor.getDefaultAddressCommandPipe()
               .createObservableResult(new GetDefaultAddressCommand())
               .map(defaultAddressAction -> createBankCard(defaultAddressAction.getResult()));
      } else {
         return Observable.just(createBankCard(manualAddressInfo));
      }
   }

   private Observable<BankCard> pushBankCard(BankCard bankCard) {
      //card without id -> AttachCardCommand -> card with id
      return smartCardInteractor.addRecordPipe()
            .createObservableResult(new AttachCardCommand(bankCard, setAsDefaultCard))
            .map(Command::getResult);
   }

   private BankCard createBankCard(AddressInfo address) {
      return ImmutableBankCard.copyOf(bankCard)
            .withCvv(Integer.parseInt(cvv))
            .withTitle(nickName)
            .withIssuerInfo(issuerInfo)
            .withAddressInfo(address);
   }

   private void checkCardData() throws FormatException {
      boolean validInfo = (WalletValidateHelper.validateAddressInfo(manualAddressInfo)) &&
            WalletValidateHelper.validateCardCvv(cvv, bankCard.number());

      if (!validInfo) throw new FormatException();
   }

   public static class Builder {

      private BankCard bankCard;
      private AddressInfo manualAddressInfo;
      private String cvv;
      private String nickName;
      private RecordIssuerInfo issuerInfo;
      private boolean useDefaultAddress;
      private boolean setAsDefaultAddress;
      private boolean setAsDefaultCard;

      public Builder setBankCard(BankCard bankCard) {
         this.bankCard = bankCard;
         return this;
      }

      public Builder setManualAddressInfo(AddressInfo manualAddressInfo) {
         this.manualAddressInfo = manualAddressInfo;
         return this;
      }

      public Builder setNickName(String nickName) {
         this.nickName = nickName;
         return this;
      }

      public Builder setCvv(String cvv) {
         this.cvv = cvv;
         return this;
      }

      public Builder setIssuerInfo(RecordIssuerInfo issuerInfo) {
         this.issuerInfo = issuerInfo;
         return this;
      }

      public Builder setUseDefaultAddress(boolean useDefaultAddress) {
         this.useDefaultAddress = useDefaultAddress;
         return this;
      }

      public Builder setSetAsDefaultAddress(boolean setAsDefaultAddress) {
         this.setAsDefaultAddress = setAsDefaultAddress;
         return this;
      }

      public Builder setSetAsDefaultCard(boolean setAsDefaultCard) {
         this.setAsDefaultCard = setAsDefaultCard;
         return this;
      }

      public AddBankCardCommand create() {
         return new AddBankCardCommand(bankCard, manualAddressInfo, nickName, cvv, issuerInfo,
               useDefaultAddress, setAsDefaultAddress, setAsDefaultCard);
      }
   }
}
