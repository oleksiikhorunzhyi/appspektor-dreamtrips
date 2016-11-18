package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.bank_info.GetBankInfoHttpAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableRecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class CreateBankCardCommand extends Command<BankCard> implements InjectableAction {

   @Inject @Named(JANET_API_LIB) Janet janet;
   @Inject MapperyContext mappery;

   private final Record swipedCard;

   public CreateBankCardCommand(Record swipedCard) {
      this.swipedCard = swipedCard;
   }

   @Override
   protected void run(CommandCallback<BankCard> callback) throws Throwable {
      janet.createPipe(GetBankInfoHttpAction.class)
            .createObservableResult(new GetBankInfoHttpAction(BankCardHelper.obtainIin(swipedCard.cardNumber())))
            .map(action -> mappery.convert(action.response(), RecordIssuerInfo.class))
            .map(this::createBankCard)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private BankCard createBankCard(RecordIssuerInfo recordIssuerInfo) {
      if (BankCardHelper.isAmexBank(Long.parseLong(swipedCard.cardNumber()))) {
         recordIssuerInfo = ImmutableRecordIssuerInfo.copyOf(recordIssuerInfo)
               .withFinancialService(Record.FinancialService.AMEX);
      }

      return ImmutableBankCard.builder()
            .issuerInfo(recordIssuerInfo)
            .cardNameHolder(swipedCard.title())
            .number(Long.parseLong(swipedCard.cardNumber()))
            .expiryYear(Integer.parseInt(swipedCard.expDate().substring(0, 2)))
            .expiryMonth(Integer.parseInt(swipedCard.expDate().substring(3, 5)))
            .build();
   }
}
