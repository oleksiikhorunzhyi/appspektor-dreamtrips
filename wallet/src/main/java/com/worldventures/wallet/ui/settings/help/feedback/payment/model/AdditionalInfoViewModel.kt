package com.worldventures.wallet.ui.settings.help.feedback.payment.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.worldventures.core.utils.ProjectTextUtils.isNotEmpty
import com.worldventures.wallet.BR

class AdditionalInfoViewModel : BaseObservable() {

   @get:Bindable
   var notes: String = ""
      set(notes) {
         field = notes
         notifyPropertyChanged(BR.notes)
      }

   val isDataChanged: Boolean
      get() = isNotEmpty(this.notes)

   override fun equals(other: Any?): Boolean {
      if (this === other) {
         return true
      }
      if (other == null || javaClass != other.javaClass) {
         return false
      }

      val that = other as AdditionalInfoViewModel?

      return this.notes == that!!.notes
   }

   override fun hashCode(): Int {
      return this.notes.hashCode()
   }
}
