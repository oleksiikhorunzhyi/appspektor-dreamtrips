package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard.CardType;
import static com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand.CardStackModel.StackType;

@CommandAction
public class CardStacksCommand extends Command<List<CardStacksCommand.CardStackModel>> implements InjectableAction {
   @Inject @Named(JANET_WALLET) Janet janet;

   private boolean forceUpdate;

   public static CardStacksCommand get(boolean forceUpdate) {
      return new CardStacksCommand(forceUpdate);
   }

   private CardStacksCommand(boolean forceUpdate) {
      this.forceUpdate = forceUpdate;
   }

   @Override
   protected void run(CommandCallback<List<CardStackModel>> callback) throws Throwable {
      Observable.combineLatest(janet.createPipe(CardListCommand.class)
            .createObservable(CardListCommand.get(forceUpdate))
            .compose(new ActionStateToActionTransformer<>()), janet.createPipe(FetchDefaultCardIdCommand.class)
            .createObservable(FetchDefaultCardIdCommand.fetch(forceUpdate))
            .compose(new ActionStateToActionTransformer<>()), (cardListCommand, fetchDefaultCardCommand) -> convert(cardListCommand
            .getResult(), fetchDefaultCardCommand.getResult())).subscribe(callback::onSuccess, callback::onFail);
   }

   private List<CardStackModel> convert(List<Card> cardList, String defaultCardId) {
      if (cardList == null) return new ArrayList<>();
      ArrayList<CardStackModel> result = new ArrayList<>();

      CardStackModel stack = defaultCardStack((BankCard) findDefaultCard(cardList, defaultCardId));
      if (stack != null) result.add(stack);

      List<BankCard> creditsBankCards = getBankCardsByType(cardList, CardType.CREDIT);
      List<BankCard> debitBankCards = getBankCardsByType(cardList, CardType.DEBIT);

      if (!creditsBankCards.isEmpty())
         result.add(new CardStackModel(CardStackModel.StackType.CREDIT, creditsBankCards));
      if (!debitBankCards.isEmpty()) result.add(new CardStackModel(StackType.DEBIT, debitBankCards));

      return result;
   }

   private Card findDefaultCard(List<Card> cardList, String defaultCardId) {
      if (defaultCardId == null) {
         return null;
      }
      return Queryable.from(cardList)
            .firstOrDefault(card -> card != null && card.id() != null && defaultCardId.equals(card.id()));
   }

   private CardStackModel defaultCardStack(BankCard defaultCard) {
      if (defaultCard == null) return null;
      ArrayList<BankCard> bankCards = new ArrayList<>();
      bankCards.add(defaultCard);

      return new CardStackModel(StackType.DEFAULT, bankCards);
   }

   protected List<BankCard> getBankCardsByType(List<Card> cardList, CardType cardType) {
      if (cardList == null) return Collections.emptyList();
      return Queryable.from(cardList)
            .filter(it -> it instanceof BankCard)
            .map(it -> (BankCard) it)
            .filter(it -> it.cardType() == cardType)
            .toList();
   }

   public static class CardStackModel {
      private StackType stackStackType;
      private List<BankCard> bankCards;

      public CardStackModel(StackType stackStackType, List<BankCard> bankCard) {
         this.stackStackType = stackStackType;
         this.bankCards = bankCard;
      }

      public List<BankCard> bankCards() {
         return bankCards;
      }

      public StackType type() {
         return stackStackType;
      }

      public enum StackType {
         DEBIT, CREDIT, DEFAULT
      }
   }
}
