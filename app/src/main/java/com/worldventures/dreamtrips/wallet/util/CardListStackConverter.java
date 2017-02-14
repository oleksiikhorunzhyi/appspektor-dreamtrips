package com.worldventures.dreamtrips.wallet.util;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.BankCardViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.CardStackViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.worldventures.dreamtrips.wallet.ui.dashboard.util.CardStackViewModel.StackType.PAYMENT;

public class CardListStackConverter {

   private final String cardStackTitle;

   public CardListStackConverter(String cardStackTitle) {
      this.cardStackTitle = cardStackTitle;
   }

   public List<CardStackViewModel> convertToModelViews(List<Card> loadedCards, String defaultCardId) {
      List<CardStackViewModel> list = new ArrayList<>();
      if (!loadedCards.isEmpty()) {
         List<BankCard> bankCards = Queryable.from(loadedCards)
               .filter((element, index) -> element.category() != Card.Category.DISCOUNT)
               .cast(BankCard.class).toList();
         list.add(obtainPaymentCardsStack(bankCards, defaultCardId));
      }
      return list;
   }

   private CardStackViewModel obtainPaymentCardsStack(List<BankCard> bankCards, String defaultCardId) {
      List<BankCardViewModel> bankCardViewModels = Queryable.from(bankCards)
            .map(element -> new BankCardViewModel(element,
                  CardUtils.isRealCardId(defaultCardId) && TextUtils.equals(element.id(), defaultCardId)))
            .toList();

      return new CardStackViewModel(PAYMENT, bankCardViewModels, cardStackTitle);
   }


}
