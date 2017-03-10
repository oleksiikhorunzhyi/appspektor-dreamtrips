package com.worldventures.dreamtrips.wallet.util;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
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

   public List<CardStackViewModel> convertToModelViews(List<Record> loadedCards, String defaultCardId) {
      List<CardStackViewModel> list = new ArrayList<>();
      if (!loadedCards.isEmpty()) {
         list.add(obtainPaymentCardsStack(loadedCards, defaultCardId));
      }
      return list;
   }

   private CardStackViewModel obtainPaymentCardsStack(List<Record> records, String defaultCardId) {
      List<BankCardViewModel> bankCardViewModels = Queryable.from(records)
            .map(element -> new BankCardViewModel(element,
                  WalletRecordUtil.isRealRecordId(defaultCardId) && TextUtils.equals(element.id(), defaultCardId)))
            .toList();

      return new CardStackViewModel(PAYMENT, bankCardViewModels, cardStackTitle);
   }

}
