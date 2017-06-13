package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CardGroupHeaderModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.worldventures.dreamtrips.wallet.domain.entity.record.RecordType.PREFERENCE;
import static com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel.StackType.LOYALTY;
import static com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel.StackType.PAYMENT;

public class CardListStackConverter {

   private WalletRecordUtil utils;
   private int index = 0;

   public CardListStackConverter(Context context) {
      this.utils = new WalletRecordUtil(context);
   }

   public List<BaseViewModel> mapToViewModel(List<Record> loadedCards, String defaultCardId) {

      if (loadedCards == null) {
         new ArrayList<>();
      }

      index = 0;
      List<CommonCardViewModel> commonCardViewModels =
            Queryable.from(loadedCards)
                  .sort((o1, o2) -> o1.recordType().compareTo(o2.recordType()))
                  .sort((o1, o2) ->
                        Boolean.compare(isCardDefault(defaultCardId, o2), isCardDefault(defaultCardId, o1)))
                  .map(loadedCard -> {
                     CommonCardViewModel model = createCommonCardViewModel(defaultCardId, loadedCard);
                     index++;
                     return model;
                  })
                  .toList();

      List<BaseViewModel> viewModels = new ArrayList<>();
      CommonCardViewModel.StackType currentType = LOYALTY;
      for (int i = 0; i < commonCardViewModels.size(); i++) {
         if (!commonCardViewModels.get(i).getCardType().equals(currentType)) {
            currentType = commonCardViewModels.get(i).getCardType();
            viewModels.add(new CardGroupHeaderModel(currentType));
         }
         viewModels.add(commonCardViewModels.get(i));
      }
      return viewModels;
   }

   @NonNull
   private CommonCardViewModel createCommonCardViewModel(String defaultCardId, Record loadedCard) {
      return new CommonCardViewModel(
                              loadedCard.id(),
                              utils.toBoldSpannable(loadedCard.nickName()),
                              setCardType(loadedCard.recordType().name()),
                              loadedCard.recordType().name(),
                              isCardDefault(defaultCardId, loadedCard),
                              utils.obtainShortCardNumber(loadedCard.numberLastFourDigits()),
                              WalletRecordUtil.fetchFullName(loadedCard),
                              utils.obtainFullCardNumber(loadedCard.numberLastFourDigits()),
                              utils.goodThrough(loadedCard.expDate()),
                              index % 2 == 0
                        );
   }

   private CommonCardViewModel.StackType setCardType(String name) {
      return name.equals(PREFERENCE) ? LOYALTY : PAYMENT;
   }

   private boolean isCardDefault(String defaultCardId, Record loadedCard) {
      if (defaultCardId == null || defaultCardId.isEmpty()) {
         return false;
      }
      if (loadedCard == null || loadedCard.id() == null) {
         return false;
      }
      return defaultCardId.equals(loadedCard.id());
   }

}
