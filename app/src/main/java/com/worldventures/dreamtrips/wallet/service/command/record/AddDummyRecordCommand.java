package com.worldventures.dreamtrips.wallet.service.command.record;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableRecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.DefaultCardIdCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class AddDummyRecordCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;

   private final BankCard dummyCard1;
   private final BankCard dummyCard2;
   private final boolean onlyToCache;

   public AddDummyRecordCommand(SmartCardUser user, boolean onlyToCache) {
      this.onlyToCache = onlyToCache;
      final String userLastName = user.lastName();
      final String userMiddleName = user.middleName();
      final String userFirstName = user.firstName();

      dummyCard1 = ImmutableBankCard.builder()
            .id("0")
            .number("9999999999994984")
            .expDate("02/19")
            .cvv("748")
            .track1("B1234567890123445^FLYE/TEST CARD^23045211000000827000000")
            .track2("1234567890123445=230452110000827")
            .nickName("Credit Card")
            .cardHolderLastName(userLastName)
            .cardHolderMiddleName(userMiddleName)
            .cardHolderFirstName(userFirstName)
            .issuerInfo(ImmutableRecordIssuerInfo.builder()
                  .bankName("Credit Card")
                  .build())
            .build();
      dummyCard2 = ImmutableBankCard.builder()
            .id("1")
            .number("9999_9999_9999_9274")
            .expDate("06/21")
            .cvv("5827")
            .track1("B1234567890123445^FLYE/TEST CARD^23045211000000827000000")
            .track2("1234567890123445=230452110000827")
            .cardHolderLastName(userLastName)
            .cardHolderMiddleName(userMiddleName)
            .cardHolderFirstName(userFirstName)
            .nickName("Credit Card")
            .issuerInfo(ImmutableRecordIssuerInfo.builder()
                  .bankName("Credit Card")
                  .build())
            .build();
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      // !!!! first card should be default !!!
      if (onlyToCache) { // because synchronization of sample card is broken
         smartCardInteractor.cardsListPipe()
               .createObservableResult(CardListCommand.add(dummyCard1))
               .flatMap(c -> smartCardInteractor.cardsListPipe()
                     .createObservableResult(CardListCommand.add(dummyCard2)))
               .flatMap(c-> smartCardInteractor.defaultCardIdPipe()
                     .createObservableResult(DefaultCardIdCommand.set("0")))
               .map(command -> (Void) null)
               .onErrorReturn(throwable -> null)
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         addDummyCard(dummyCard1, true)
               .flatMap(it -> addDummyCard(dummyCard2, false))
               .subscribe(callback::onSuccess, callback::onFail);
      }
   }

   private Observable<Void> addDummyCard(BankCard dummyCard, boolean isDefault) {
      return smartCardInteractor.addRecordPipe()
            .createObservableResult(new AttachCardCommand(dummyCard, isDefault))
            .map(command -> (Void) null)
            .onErrorReturn(throwable -> null);
   }

}
