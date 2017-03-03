package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.ActionType;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.TokenizationCardAction;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.TokenizeBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.NxtMultifunctionException;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateAddressInfoOrThrow;
import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateCardNameOrThrow;
import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateCvvOrThrow;

@CommandAction
public class AddBankCardCommand extends Command<BankCard> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject NxtInteractor nxtInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SnappyRepository snappyRepository;

   private final BankCard bankCard;
   private final AddressInfo manualAddressInfo;
   private final String cvv;
   private final String cardName;
   private final RecordIssuerInfo issuerInfo;
   private final boolean setAsDefaultAddress;
   private final boolean useDefaultAddress;
   private final boolean setAsDefaultCard;

   private AddBankCardCommand(BankCard bankCard,
         AddressInfo manualAddressInfo,
         String cardName,
         String cvv,
         RecordIssuerInfo issuerInfo,
         boolean useDefaultAddress,
         boolean setAsDefaultAddress,
         boolean setAsDefaultCard
   ) {
      this.issuerInfo = issuerInfo;
      this.setAsDefaultAddress = setAsDefaultAddress;
      this.useDefaultAddress = useDefaultAddress;
      this.cardName = cardName;
      this.cvv = cvv;
      this.bankCard = bankCard;
      this.manualAddressInfo = manualAddressInfo;
      this.setAsDefaultCard = setAsDefaultCard;
   }

   @Override
   protected void run(CommandCallback<BankCard> callback) throws Throwable {
      checkCardData();

      createCardWithAddress()
            .flatMap(this::tokenizeBankCard)
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

   private Observable<NxtBankCard> tokenizeBankCard(BankCard bankCard) {
      return nxtInteractor.tokenizeBankCardPipe()
            .createObservableResult(new TokenizeBankCardCommand(bankCard))
            .map(Command::getResult)
            .doOnNext(this::sendTokenizationAnalytics)
            .flatMap(nxtBankCard -> {
               if (nxtBankCard.getResponseErrors().isEmpty()) {
                  return Observable.just(nxtBankCard);
               } else {
                  return Observable.error(new NxtMultifunctionException(
                        NxtBankCardHelper.getResponseErrorMessage(nxtBankCard.getResponseErrors())));
               }
            });
   }

   private void sendTokenizationAnalytics(NxtBankCard nxtBankCard) {
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(
            TokenizationCardAction.from(nxtBankCard, ActionType.ADD, true)
      ));
   }

   private Observable<BankCard> pushBankCard(NxtBankCard bankCard) {
      //card without id -> AttachCardCommand -> card with id
      return smartCardInteractor.addRecordPipe()
            .createObservableResult(new AttachCardCommand(bankCard, setAsDefaultCard))
            .map(Command::getResult);
   }

   private BankCard createBankCard(AddressInfo address) {
      return ImmutableBankCard.builder()
            .from(bankCard)
            .cvv(cvv)
            .nickName(cardName)
            .issuerInfo(issuerInfo)
            .addressInfo(address)
            .build();
   }

   private void checkCardData() throws FormatException {
      validateCardNameOrThrow(cardName);
      validateAddressInfoOrThrow(manualAddressInfo);
      validateCvvOrThrow(cvv, bankCard.number());
   }

   public static class Builder {

      private BankCard bankCard;
      private AddressInfo manualAddressInfo;
      private String cvv;
      private String cardName;
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

      public Builder setCardName(String cardName) {
         this.cardName = cardName;
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
         return new AddBankCardCommand(bankCard, manualAddressInfo, cardName, cvv, issuerInfo,
               useDefaultAddress, setAsDefaultAddress, setAsDefaultCard);
      }
   }
}
