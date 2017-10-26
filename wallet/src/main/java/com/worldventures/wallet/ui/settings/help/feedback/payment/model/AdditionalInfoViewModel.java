package com.worldventures.wallet.ui.settings.help.feedback.payment.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.Nullable;

import com.worldventures.wallet.BR;

import static com.worldventures.core.utils.ProjectTextUtils.isNotEmpty;

public class AdditionalInfoViewModel extends BaseObservable {

   @Nullable
   private String notes;

   @Bindable
   @Nullable
   public String getNotes() {
      return notes;
   }

   public void setNotes(@Nullable String notes) {
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

   public boolean isDataChanged() {
      return isNotEmpty(notes);
   }
}
