package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.GetMemberRecordsAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;
import rx.functions.Func1;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class CardListCommand extends Command<List<Card>> implements InjectableAction, CachedAction<List<Card>> {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject MapperyContext mapperyContext;

   private final Func1<List<Card>, Observable<List<Card>>> operationFunc;
   private final boolean shouldBeCached;
   private final boolean forceUpdate;

   private volatile List<Card> cachedItems;

   public static CardListCommand get(boolean forceUpdate) {
      return new CardListCommand(forceUpdate);
   }

   public static CardListCommand remove(String cardId) {
      return new CardListCommand(new RemoveOperationFunc(cardId));
   }

   public static CardListCommand add(Card card) {
      return new CardListCommand(new AddOperationFunc(card));
   }

   public static CardListCommand edit(Card card) {
      return new CardListCommand(new EditOperationFunc(card));
   }

   private CardListCommand(Func1<List<Card>, Observable<List<Card>>> operationFunc) {
      this.operationFunc = operationFunc;
      this.shouldBeCached = true;
      this.forceUpdate = false;
   }

   private CardListCommand(boolean forceUpdate) {
      this.forceUpdate = forceUpdate;
      this.operationFunc = null;
      this.shouldBeCached = false;
   }

   @Override
   protected void run(CommandCallback<List<Card>> callback) throws Throwable {
      Observable<List<Card>> listObservable =
            ((!isAddOperation()) && (!isCachePresent())) || forceUpdate ? fetchFakeFromDevice() : Observable.just(cachedItems);

      if (operationFunc != null) {
         listObservable = listObservable
               .map(list -> new ArrayList<>(list))
               .flatMap(operationFunc);
      }
      listObservable.subscribe(callback::onSuccess, callback::onFail);
   }

   private boolean isAddOperation() {
      return operationFunc != null && operationFunc.getClass() == AddOperationFunc.class;
   }

   private Observable<List<Card>> fetchFromDevice() {
      return janet.createPipe(GetMemberRecordsAction.class)
            .createObservableResult(new GetMemberRecordsAction())
            .flatMap(action -> Observable.from(action.records)
                  .map(record -> (Card) mapperyContext.convert(record, BankCard.class))
                  .toList());
   }

   private Observable<List<Card>> fetchFakeFromDevice() {
      return Observable.just(Collections.emptyList());
   }

   @Override
   public List<Card> getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, List<Card> cache) {
      if (cache == null) cachedItems = Collections.emptyList();
      else cachedItems = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .saveToCache(shouldBeCached)
            .build();
   }

   private static final class RemoveOperationFunc implements Func1<List<Card>, Observable<List<Card>>> {
      private String cardId;

      RemoveOperationFunc(String cardId) {
         this.cardId = cardId;
      }

      @Override
      public Observable<List<Card>> call(List<Card> cards) {
         cards.remove(Queryable.from(cards).first(element -> element.id().equals(cardId)));
         return Observable.just(cards);
      }
   }

   private static final class AddOperationFunc implements Func1<List<Card>, Observable<List<Card>>> {
      private Card card;

      AddOperationFunc(Card card) {
         this.card = card;
      }

      @Override
      public Observable<List<Card>> call(List<Card> cards) {
         cards.add(card);
         return Observable.just(cards);
      }
   }

   private static final class EditOperationFunc implements Func1<List<Card>, Observable<List<Card>>> {
      private Card card;

      EditOperationFunc(Card card) {
         this.card = card;
      }

      @Override
      public Observable<List<Card>> call(List<Card> cards) {
         Card cardInStack = Queryable.from(cards).firstOrDefault(element -> element.id().equals(card.id()));
         int position = cardInStack == null ? -1 : cards.indexOf(cardInStack);
         if (position != -1) cards.set(position, card);
         return Observable.just(cards);
      }
   }

   private boolean isCachePresent() {
      return cachedItems != null && !cachedItems.isEmpty();
   }
}
