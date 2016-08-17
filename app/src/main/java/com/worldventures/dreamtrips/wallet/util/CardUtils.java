package com.worldventures.dreamtrips.wallet.util;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;

import java.util.List;

public class CardUtils {

   private CardUtils() {
   }

   public static int stacksToItemsCount(List<CardStackViewModel> items) {
      if (items == null) return 0;
      Integer sum = Queryable.from(items)
            .filter(stack -> stack.getType() != CardStacksCommand.CardStackModel.StackType.DEFAULT)
            .sum(stack -> stack.getCardList() != null ? stack.getCardList().size() : 0);
      return sum != null ? sum : 0;
   }

   public static boolean isRealCardId(String cardId) {
      return !TextUtils.equals(cardId, "0");
   }

}
