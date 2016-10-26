package com.worldventures.dreamtrips.wallet.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;

import java.util.List;

public class CardUtils {

   private CardUtils() {
   }

   public static int stacksToItemsCount(List<CardStackViewModel> items) {
      if (items == null) return 0;
      Integer sum = Queryable.from(items)
            .sum(stack -> stack.getCardList() != null ? stack.getCardList().size() : 0);
      return sum != null ? sum : 0;
   }

   public static boolean isRealCard(@Nullable Card card) {
      return card != null && isRealCardId(card.id());
   }

   public static boolean isRealCardId(@Nullable String cardId) {
      return cardId != null;
   }

   public static boolean equals(@Nullable Card card1, @Nullable Card card2) {
      if (card1 == null || card2 == null) return false;
      return TextUtils.equals(card1.id(), card2.id());
   }

}
