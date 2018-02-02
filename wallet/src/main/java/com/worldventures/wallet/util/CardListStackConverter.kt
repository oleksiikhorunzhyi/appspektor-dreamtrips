package com.worldventures.wallet.util

import android.content.Context
import com.innahema.collections.query.queriables.Queryable
import com.worldventures.wallet.R
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.domain.entity.record.RecordType.PREFERENCE
import com.worldventures.wallet.ui.common.adapter.BaseViewModel
import com.worldventures.wallet.ui.dashboard.util.model.CardGroupHeaderModel
import com.worldventures.wallet.ui.dashboard.util.model.CommonCardViewModel
import com.worldventures.wallet.ui.dashboard.util.model.CommonCardViewModel.StackType.LOYALTY
import com.worldventures.wallet.ui.dashboard.util.model.CommonCardViewModel.StackType.PAYMENT
import java.util.ArrayList

class CardListStackConverter(private val utils: WalletRecordUtil, private val featureHelper: WalletFeatureHelper) {

   private var index = 0

   private val cardBackGroundResId: Int
      get() = if (index % 2 == 0) R.drawable.wallet_card_dark_blue_background else R.drawable.wallet_card_blue_background

   fun mapToViewModel(context: Context, loadedCards: List<Record>?, defaultCardId: String?): ArrayList<BaseViewModel<*>> {

      if (loadedCards == null) {
         return ArrayList()
      }

      index = 0
      val commonCardViewModels = Queryable.from(loadedCards)
            .sort { record1, record2 -> record1.recordType.compareTo(record2.recordType) }
            .sort { o1, o2 -> compare(isCardDefault(defaultCardId, o2), isCardDefault(defaultCardId, o1)) }
            .map { loadedCard ->
               val model = createCommonCardViewModel(context, loadedCard, isCardDefault(defaultCardId, loadedCard))
               index++
               model
            }
            .toList()

      val viewModels = ArrayList<BaseViewModel<*>>()
      var currentType: CommonCardViewModel.StackType = LOYALTY
      for (i in commonCardViewModels.indices) {
         if (commonCardViewModels[i].cardType != currentType) {
            currentType = commonCardViewModels[i].cardType
            viewModels.add(CardGroupHeaderModel(currentType))
         }
         viewModels.add(commonCardViewModels[i])
      }
      return viewModels
   }

   // Boolean.compare is added in API 19
   private fun compare(x: Boolean, y: Boolean): Int {
      return if (x == y) 0 else if (x) 1 else -1
   }

   @Suppress("UnsafeCallOnNullableType")
   private fun createCommonCardViewModel(context: Context, loadedCard: Record, isDefault: Boolean): CommonCardViewModel {
      return CommonCardViewModel(
            loadedCard.id!!,
            utils.toBoldSpannable(loadedCard.nickname),
            setCardType(loadedCard.recordType.name),
            loadedCard.recordType.name,
            isDefault,
            utils.obtainShortCardNumber(loadedCard.numberLastFourDigits),
            WalletRecordUtil.fetchFullName(loadedCard),
            utils.obtainFullCardNumber(loadedCard.numberLastFourDigits),
            utils.goodThrough(context, loadedCard.expDate),
            cardBackGroundResId,
            featureHelper.isSampleCardMode
      )
   }

   private fun setCardType(name: String): CommonCardViewModel.StackType {
      return if (name == PREFERENCE.name) LOYALTY else PAYMENT
   }

   private fun isCardDefault(defaultCardId: String?, loadedCard: Record?): Boolean {
      if (defaultCardId.isNullOrEmpty()) {
         return false
      }
      val loadedCardId = loadedCard?.id
      return loadedCardId == defaultCardId
   }

}
