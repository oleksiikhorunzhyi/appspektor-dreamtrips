package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.worldventures.dreamtrips.BR;

public class AdditionalInfoViewModel extends BaseObservable {

   private String notes;

   @Bindable
   public String getNotes() {
      return notes;
   }

   public void setNotes(String notes) {
      this.notes = notes;
      notifyPropertyChanged(BR.notes);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      AdditionalInfoViewModel that = (AdditionalInfoViewModel) o;

      return notes != null ? notes.equals(that.notes) : that.notes == null;

   }

   @Override
   public int hashCode() {
      return notes != null ? notes.hashCode() : 0;
   }
}
