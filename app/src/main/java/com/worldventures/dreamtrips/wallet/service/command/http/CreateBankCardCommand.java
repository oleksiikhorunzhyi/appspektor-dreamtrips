package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FinancialService;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableRecordIssuerInfo;
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
      BankCard bankCard = mappery.convert(swipedCard, BankCard.class);
      callback.onSuccess(withExtraInfo(bankCard));
   }

   private BankCard withExtraInfo(BankCard bankCard) {
      final ImmutableBankCard.Builder builder = ImmutableBankCard.builder()
            .from(bankCard)
            .numberLastFourDigits(BankCardHelper.obtainLastCardDigits(bankCard.number()));

      if (BankCardHelper.isAmexBank(bankCard.number())) {
         builder.issuerInfo(ImmutableRecordIssuerInfo.builder()
               .from(bankCard.issuerInfo())
               .financialService(FinancialService.AMEX).build());
      }
      return builder.build();
   }

}