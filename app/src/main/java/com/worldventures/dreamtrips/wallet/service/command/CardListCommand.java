package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public class CardListCommand extends CachedValueCommand<List<Card>> {

   public static CardListCommand fetch() {
      return new CardListCommand();
   }

   public static CardListCommand remove(String cardId) {
      return new CardListCommand(new RemoveOperationFunc(cardId));
   }

   public static CardListCommand add(Card card) {
      return new CardListCommand(new AddOperationFunc(card));
   }

   public static CardListCommand replace(Card card) {
      return new CardListCommand(new EditOperationFunc(card));
   }

   public static CardListCommand edit(Card card) {
      return new CardListCommand(new EditOperationFunc(card));
   }

   public CardListCommand() {
   }

   public CardListCommand(Func1<List<Card>, List<Card>> operationFunc) {
      super(cards -> operationFunc.call(new ArrayList<>(cards)));
   }

   private static final class RemoveOperationFunc implements Func1<List<Card>, List<Card>> {
      private String cardId;

      RemoveOperationFunc(String cardId) {
         this.cardId = cardId;
      }

      @Override
      public List<Card> call(List<Card> cards) {
         cards.remove(Queryable.from(cards).first(element -> element.id().equals(cardId)));
         return cards;
      }
   }

   private static final class AddOperationFunc implements Func1<List<Card>, List<Card>> {
      private Card card;

      AddOperationFunc(Card card) {
         this.card = card;
      }

      @Override
      public List<Card> call(List<Card> cards) {
         cards.add(card);
         return cards;
      }
   }

   private static final class EditOperationFunc implements Func1<List<Card>, List<Card>> {
      private Card card;

      EditOperationFunc(Card card) {
         this.card = card;
      }

      @Override
      public List<Card> call(List<Card> cards) {
         return !cards.isEmpty() ? Queryable.from(cards).map(this::remapCard).toList(): cards;
      }

      private Card remapCard(Card element) {
         if (element.id().equals(card.id())) {
            return card;
         } else {
            return element;
         }
      }
   }
}
