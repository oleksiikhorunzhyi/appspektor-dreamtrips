package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;

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
      List<BankCard> bankCards = Queryable.from(cardList).map(element -> (BankCard) element).toList();
      return bankCards.isEmpty() ? Collections.emptyList() :
            Collections.singletonList(new CardStackModel(CardStackModel.StackType.PAYMENT, bankCards, defaultCardId));
   }

   public static class CardStackModel {
      public final StackType stackStackType;
      public final List<BankCard> bankCards;
      public final String defaultCardId;


      public CardStackModel(StackType stackStackType, List<BankCard> bankCards, String defaultCardId) {
         this.stackStackType = stackStackType;
         this.bankCards = bankCards;
         this.defaultCardId = defaultCardId;
      }

      public enum StackType {
         PAYMENT, LOYALTY
      }
   }
}
