package com.worldventures.wallet.ui.records.detail

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcel
import android.os.Parcelable

import com.worldventures.wallet.BR
import com.worldventures.wallet.ui.dashboard.util.model.CommonCardViewModel

class RecordDetailViewModel(
      val recordModel: CommonCardViewModel,
      private var _nameInputError: String = "",
      private var _saveButtonEnabled: Boolean = false,
      var originCardName: String = recordModel.cardName.toString(),
      var defaultRecordDetail: DefaultRecordDetail? = null
) : BaseObservable(), Parcelable {

   val recordId: String
      get() = recordModel.recordId

   var recordName: String
      @Bindable get() = recordModel.cardName.toString()
      set(cardName) {
         recordModel.cardName = cardName
         notifyPropertyChanged(BR.recordName)
      }

   var nameInputError: String
      @Bindable get() = _nameInputError
      set(value) {
         _nameInputError = value
         notifyPropertyChanged(BR.nameInputError)
      }

   var isSaveButtonEnabled: Boolean
      @Bindable get() = _saveButtonEnabled
      set(value) {
         _saveButtonEnabled = value
         notifyPropertyChanged(BR.saveButtonEnabled)
      }

   constructor(source: Parcel) : this(
         source.readParcelable<CommonCardViewModel>(CommonCardViewModel::class.java.classLoader),
         source.readString(),
         1 == source.readInt(),
         source.readString(),
         source.readParcelable<DefaultRecordDetail>(DefaultRecordDetail::class.java.classLoader)
   )

   override fun describeContents() = 0

   override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
      writeParcelable(recordModel, 0)
      writeString(_nameInputError)
      writeInt(if (_saveButtonEnabled) 1 else 0)
      writeString(originCardName)
      writeParcelable(defaultRecordDetail, 0)
   }

   companion object {
      @JvmField
      val CREATOR: Parcelable.Creator<RecordDetailViewModel> = object : Parcelable.Creator<RecordDetailViewModel> {
         override fun createFromParcel(source: Parcel) = RecordDetailViewModel(source)
         override fun newArray(size: Int): Array<RecordDetailViewModel?> = arrayOfNulls(size)
      }
   }
}
