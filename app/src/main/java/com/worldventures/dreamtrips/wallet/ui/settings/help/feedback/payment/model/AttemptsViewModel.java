package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.worldventures.dreamtrips.BR;

public class AttemptsViewModel extends BaseObservable {

   private boolean isSuccessPayment = true;
   private int countOfAttempts = 1;

   public boolean isSuccessPayment() {
      return isSuccessPayment;
   }

   public void setSuccessPayment(boolean successPayment) {
      isSuccessPayment = successPayment;
   }

   @Bindable
   public int getCountOfAttempts() {
      return countOfAttempts;
   }

   public void setCountOfAttempts(int countOfAttempts) {
      this.countOfAttempts = countOfAttempts;
      notifyPropertyChanged(BR.countOfAttempts);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      AttemptsViewModel that = (AttemptsViewModel) o;

      if (isSuccessPayment != that.isSuccessPayment) return false;
      return countOfAttempts == that.countOfAttempts;

   }

   @Override
   public int hashCode() {
      int result = (isSuccessPayment ? 1 : 0);
      result = 31 * result + countOfAttempts;
      return result;
   }
}