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
import rx.Observable;

@CommandAction
public class AddDummyCardCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;

   private final BankCard dummyCard1 = ImmutableBankCard.builder()
         .id("sample1")
         .number(9999_9999_9999_4984L)
         .expiryMonth(2)
         .expiryYear(19)
         .category(Card.Category.SAMPLE)
         .title("Shirly A Temple")
         .issuerInfo(ImmutableRecordIssuerInfo.builder()
               .bankName("Credit Card")
               .build())
         .build();

   private final BankCard dummyCard2 = ImmutableBankCard.builder()
         .id("sample2")
         .number(9999_9999_9999_9274L)
         .expiryMonth(2)
         .expiryYear(19)
         .category(Card.Category.SAMPLE)
         .title("Shirly A Temple")
         .issuerInfo(ImmutableRecordIssuerInfo.builder()
               .bankName("Credit Card")
               .build())
         .build();

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.zip(
            smartCardInteractor.addRecordPipe().createObservableResult(new AttachCardCommand(dummyCard1, true)), // first card should be default
            smartCardInteractor.addRecordPipe().createObservableResult(new AttachCardCommand(dummyCard2, false)),
            (attachCardCommand, attachCardCommand2) -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
