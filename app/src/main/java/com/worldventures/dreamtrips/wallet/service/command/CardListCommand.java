package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

   public static CardListCommand addAll(List<? extends Card> cards) {
      return new CardListCommand(new AddOperationFunc(cards));
   }

   public static CardListCommand replace(List<? extends Card> cardList) {
      return new CardListCommand(new ReplaceCardsOperationFunc(cardList));
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

      private final String cardId;

      RemoveOperationFunc(String cardId) {
         this.cardId = cardId;
      }

      @Override
      public List<Card> call(List<Card> cards) {
         cards.remove(Queryable.from(cards).first(element -> Objects.equals(element.id(), cardId)));
         return cards;
      }
   }

   private static final class AddOperationFunc implements Func1<List<Card>, List<Card>> {

      private final List<Card> cardsToAdd = new ArrayList<>();

      AddOperationFunc(Card card) {
         cardsToAdd.add(card);
      }

      AddOperationFunc(List<? extends Card> cards) {
         cardsToAdd.addAll(cards);
      }

      @Override
      public List<Card> call(List<Card> cards) {
         cards.addAll(cardsToAdd);
         return cards;
      }
   }

   private static final class ReplaceCardsOperationFunc implements Func1<List<Card>, List<Card>> {

      private final List<Card> newCards = new ArrayList<>();

      ReplaceCardsOperationFunc(List<? extends Card> cards) {
         newCards.addAll(cards);
      }

      @Override
      public List<Card> call(List<Card> cards) {
         cards.clear();
         cards.addAll(newCards);
         return cards;
      }
   }

   private static final class EditOperationFunc implements Func1<List<Card>, List<Card>> {

      private final Card editedCard;

      EditOperationFunc(Card card) {
         editedCard = card;
      }

      @Override
      public List<Card> call(List<Card> cards) {
         return !cards.isEmpty() ? Queryable.from(cards).map(this::remapCard).toList() : cards;
      }

      private Card remapCard(Card element) {
         return Objects.equals(element.id(), editedCard.id()) ? editedCard : element;
      }
   }

}