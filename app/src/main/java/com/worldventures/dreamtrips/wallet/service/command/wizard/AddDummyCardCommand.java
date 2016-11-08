package com.worldventures.dreamtrips.wallet.service.command.wizard;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableRecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.model.Record;
import rx.Observable;

@CommandAction
public class AddDummyCardCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   private final BankCard dummyCard1;
   private final BankCard dummyCard2;

   public AddDummyCardCommand(String userName) {
      dummyCard1 = ImmutableBankCard.builder()
            .id("101")
            .number(9999_9999_9999_4984L)
            .expiryMonth(2)
            .expiryYear(19)
            .cvv(748)
            .track1("B1234567890123445^FLYE/TEST CARD^23045211000000827000000")
            .track2("1234567890123445=230452110000827")
            .category(Card.Category.SAMPLE)
            .title(userName)
            .issuerInfo(ImmutableRecordIssuerInfo.builder()
                  .bankName("Credit Card")
                  .financialService(Record.FinancialService.SAMPLE)
                  .build())
            .build();
      dummyCard2 = ImmutableBankCard.builder()
            .id("102")
            .number(9999_9999_9999_9274L)
            .expiryMonth(6)
            .expiryYear(21)
            .cvv(5827)
            .track1("B1234567890123445^FLYE/TEST CARD^23045211000000827000000")
            .track2("1234567890123445=230452110000827")
            .category(Card.Category.SAMPLE)
            .title(userName)
            .issuerInfo(ImmutableRecordIssuerInfo.builder()
                  .bankName("Credit Card")
                  .financialService(Record.FinancialService.SAMPLE)
                  .build())
            .build();
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      // first card should be default
      addDummyCard(dummyCard1, true)
            .flatMap(it -> addDummyCard(dummyCard2, false))
            .map(it -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<AttachCardCommand> addDummyCard(BankCard dummyCard, boolean isDefault) {
      return smartCardInteractor.addRecordPipe()
            .createObservableResult(new AttachCardCommand(dummyCard, isDefault));
   }
}
