package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.worldventures.dreamtrips.BR;
import com.worldventures.dreamtrips.wallet.domain.entity.settings.payment_feedback.PaymentType;

public class PaymentTerminalViewModel extends BaseObservable {

   private String terminalNameModel;
   private boolean isMagneticSwipe = true;

   @Bindable
   public String getTerminalNameModel() {
      return terminalNameModel;
   }

   public void setTerminalNameModel(String terminalNameModel) {
      this.terminalNameModel = terminalNameModel;
      notifyPropertyChanged(BR.terminalNameModel);
   }

   @Bindable
   public boolean isMagneticSwipe() {
      return isMagneticSwipe;
   }

   public void setMagneticSwipe(boolean magneticSwipe) {
      isMagneticSwipe = magneticSwipe;
      notifyPropertyChanged(BR.magneticSwipe);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      PaymentTerminalViewModel that = (PaymentTerminalViewModel) o;

      if (isMagneticSwipe != that.isMagneticSwipe) return false;
      return terminalNameModel != null ? terminalNameModel.equals(that.terminalNameModel) : that.terminalNameModel == null;

   }

   @Override
   public int hashCode() {
      int result = terminalNameModel != null ? terminalNameModel.hashCode() : 0;
      result = 31 * result + (isMagneticSwipe ? 1 : 0);
      return result;
   }

   public String getPaymentType() {
      return isMagneticSwipe ? PaymentType.MAG_STRIPE_SWIPE.type() : PaymentType.WIRELESS_MAGNETIC_SWIPE.type();
   }
}
