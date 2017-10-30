package com.worldventures.wallet.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.wallet.R;
import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.wallet.ui.dashboard.util.model.CardGroupHeaderModel;
import com.worldventures.wallet.ui.dashboard.util.model.CommonCardViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.worldventures.wallet.domain.entity.record.RecordType.PREFERENCE;
import static com.worldventures.wallet.ui.dashboard.util.model.CommonCardViewModel.StackType.LOYALTY;
import static com.worldventures.wallet.ui.dashboard.util.model.CommonCardViewModel.StackType.PAYMENT;

public class CardListStackConverter {

   private final WalletRecordUtil utils;
   private final WalletFeatureHelper featureHelper;

   private int index = 0;

   public CardListStackConverter(WalletRecordUtil recordUtil, WalletFeatureHelper featureHelper) {
      this.utils = recordUtil;
      this.featureHelper = featureHelper;
   }

   public ArrayList<BaseViewModel> mapToViewModel(final Context context, List<Record> loadedCards, @Nullable String defaultCardId) {

      if (loadedCards == null) {
         return new ArrayList<>();
      }

      index = 0;
      List<CommonCardViewModel> commonCardViewModels
            = Queryable.from(loadedCards)
            .sort((o1, o2) -> o1.recordType().compareTo(o2.recordType()))
            .sort((o1, o2) -> compare(isCardDefault(defaultCardId, o2), isCardDefault(defaultCardId, o1)))
            .map(loadedCard -> {
               CommonCardViewModel model = createCommonCardViewModel(context, loadedCard, isCardDefault(defaultCardId, loadedCard));
               index++;
               return model;
            })
            .toList();

      ArrayList<BaseViewModel> viewModels = new ArrayList<>();
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

   // Boolean.compare is added in API 19
   private int compare(boolean x, boolean y) {
      return (x == y) ? 0 : (x ? 1 : -1);
   }

   @NonNull
   private CommonCardViewModel createCommonCardViewModel(Context context, Record loadedCard, boolean isDefault) {
      return new CommonCardViewModel(
            loadedCard.id(),
            utils.toBoldSpannable(loadedCard.nickName()),
            setCardType(loadedCard.recordType().name()),
            loadedCard.recordType().name(),
            isDefault,
            utils.obtainShortCardNumber(loadedCard.numberLastFourDigits()),
            WalletRecordUtil.fetchFullName(loadedCard),
            utils.obtainFullCardNumber(loadedCard.numberLastFourDigits()),
            utils.goodThrough(context, loadedCard.expDate()),
            getCardBackGroundResId(),
            featureHelper.isSampleCardMode()
      );
   }

   private int getCardBackGroundResId() {
      return index % 2 == 0 ? R.drawable.wallet_card_dark_blue_background : R.drawable.wallet_card_blue_background;
   }

   private CommonCardViewModel.StackType setCardType(String name) {
      return name.equals(PREFERENCE) ? LOYALTY : PAYMENT;
   }

   private boolean isCardDefault(@Nullable String defaultCardId, Record loadedCard) {
      if (defaultCardId == null || defaultCardId.isEmpty()) {
         return false;
      }
      if (loadedCard == null || loadedCard.id() == null) {
         return false;
      }
      return defaultCardId.equals(loadedCard.id());
   }

}
