package com.worldventures.wallet.ui.dashboard.util.model

import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.DrawableRes
import android.text.TextUtils
import android.view.View

import com.worldventures.wallet.BR
import com.worldventures.wallet.ui.common.adapter.BaseViewModel
import com.worldventures.wallet.ui.dashboard.util.adapter.DashboardHolderTypeFactory

data class CommonCardViewModel(
      val recordId: String,
      private var _cardName: CharSequence,
      val cardType: StackType,
      val cardTypeName: String,
      private var _defaultCard: Boolean,
      val cardLastDigitsShort: CharSequence,
      val cardHolderName: String,
      val cardLastDigitsLong: CharSequence,
      val goodThrough: CharSequence,
      @DrawableRes val cardBackGround: Int,
      val isSampleCard: Boolean
) : BaseViewModel<DashboardHolderTypeFactory>(), Parcelable {
   init {
      modelId = recordId + cardTypeName
   }

   var defaultCard: Boolean
      @Bindable get() = _defaultCard
      set(value) {
         _defaultCard = value
         notifyPropertyChanged(BR.defaultCard)
      }

   var cardName: CharSequence
      @Bindable get() = _cardName
      set(value) {
         _cardName = value
         notifyPropertyChanged(BR.cardName)
      }

   override fun type(typeFactory: DashboardHolderTypeFactory) = typeFactory.type(this)

   enum class StackType {
      PAYMENT, LOYALTY
   }

   constructor(source: Parcel) : this(
         source.readString(),
         TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source),
         StackType.values()[source.readInt()],
         source.readString(),
         1 == source.readInt(),
         TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source),
         source.readString(),
         TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source),
         TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source),
         source.readInt(),
         1 == source.readInt()
   )

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeString(recordId)
      TextUtils.writeToParcel(_cardName, dest, 0)
      writeInt(cardType.ordinal)
      writeString(cardTypeName)
      writeInt((if (_defaultCard) 1 else 0))
      TextUtils.writeToParcel(cardLastDigitsShort, dest, 0)
      writeString(cardHolderName)
      TextUtils.writeToParcel(cardLastDigitsLong, dest, 0)
      TextUtils.writeToParcel(goodThrough, dest, 0)
      writeInt(cardBackGround)
      writeInt((if (isSampleCard) 1 else 0))
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<CommonCardViewModel> = object : Parcelable.Creator<CommonCardViewModel> {
         override fun createFromParcel(source: Parcel): CommonCardViewModel = CommonCardViewModel(source)
         override fun newArray(size: Int): Array<CommonCardViewModel?> = arrayOfNulls(size)
      }
   }
}

@BindingAdapter("cardBackground")
fun getCardBackground(view: View, @DrawableRes backGround: Int) {
   view.setBackgroundResource(backGround)
}
