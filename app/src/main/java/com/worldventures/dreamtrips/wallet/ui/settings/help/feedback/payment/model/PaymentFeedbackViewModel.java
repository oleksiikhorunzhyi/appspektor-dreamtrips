package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.worldventures.dreamtrips.BR;

public class PaymentFeedbackViewModel extends BaseObservable {

   private AttemptsViewModel attemptsView = new AttemptsViewModel();
   private MerchantViewModel merchantView = new MerchantViewModel();
   private PaymentTerminalViewModel terminalView = new PaymentTerminalViewModel();
   private AdditionalInfoViewModel infoView = new AdditionalInfoViewModel();
   private boolean canBeLost;

   @Bindable
   public AttemptsViewModel getAttemptsView() {
      return attemptsView;
   }

   public void setAttemptsView(AttemptsViewModel attemptsView) {
      this.attemptsView = attemptsView;
      notifyPropertyChanged(BR.attemptsView);
   }

   @Bindable
   public MerchantViewModel getMerchantView() {
      return merchantView;
   }

   public void setMerchantView(MerchantViewModel merchantView) {
      this.merchantView = merchantView;
      notifyPropertyChanged(BR.merchantView);
   }

   @Bindable
   public PaymentTerminalViewModel getTerminalView() {
      return terminalView;
   }

   public void setTerminalView(PaymentTerminalViewModel terminalView) {
      this.terminalView = terminalView;
      notifyPropertyChanged(BR.terminalView);
   }

   @Bindable
   public AdditionalInfoViewModel getInfoView() {
      return infoView;
   }

   public void setInfoView(AdditionalInfoViewModel infoView) {
      this.infoView = infoView;
      notifyPropertyChanged(BR.infoView);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      PaymentFeedbackViewModel that = (PaymentFeedbackViewModel) o;

      if (attemptsView != null ? !attemptsView.equals(that.attemptsView) : that.attemptsView != null) {
         return false;
      }
      if (merchantView != null ? !merchantView.equals(that.merchantView) : that.merchantView != null) {
         return false;
      }
      if (terminalView != null ? !terminalView.equals(that.terminalView) : that.terminalView != null) {
         return false;
      }
      return infoView != null ? infoView.equals(that.infoView) : that.infoView == null;

   }

   @Override
   public int hashCode() {
      int result = attemptsView != null ? attemptsView.hashCode() : 0;
      result = 31 * result + (merchantView != null ? merchantView.hashCode() : 0);
      result = 31 * result + (terminalView != null ? terminalView.hashCode() : 0);
      result = 31 * result + (infoView != null ? infoView.hashCode() : 0);
      return result;
   }

   public void setCanBeLost(boolean canBeLost) {
      this.canBeLost = canBeLost;
   }

   public boolean isDataChanged() {
      return !canBeLost
            && (attemptsView.isDataChanged()
            || merchantView.isDataChanged()
            || terminalView.isDataChanged()
            || infoView.isDataChanged());
   }
}
