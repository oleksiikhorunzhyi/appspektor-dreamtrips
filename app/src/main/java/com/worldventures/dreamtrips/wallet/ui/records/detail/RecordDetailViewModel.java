package com.worldventures.dreamtrips.wallet.ui.records.detail;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.support.design.widget.TextInputLayout;

import com.worldventures.dreamtrips.BR;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel;

public class RecordDetailViewModel extends BaseObservable {

   private final CommonCardViewModel commonCardViewModel;
   private boolean defaultRecord;
   private CharSequence nameInputError;
   private boolean isChanged;

   public RecordDetailViewModel(CommonCardViewModel commonCardViewModel) {
      this.commonCardViewModel = commonCardViewModel;
      this.defaultRecord = commonCardViewModel.isDefaultCard();
      this.nameInputError = "";
   }

   public String getRecordId() {
      return commonCardViewModel.getRecordId();
   }

   @Bindable
   public boolean isDefaultRecord() {
      return defaultRecord;
   }

   public void setDefaultRecord(boolean isDefaultCard) {
      this.defaultRecord = isDefaultCard;
      notifyPropertyChanged(BR.defaultRecord);
   }

   @Bindable
   public String getRecordName() {
      return commonCardViewModel.getCardName().toString();
   }

   public void setRecordName(String cardName) {
      commonCardViewModel.setCardName(cardName);
      notifyPropertyChanged(BR.recordName);
   }

   @Bindable
   public CharSequence getNameInputError() {
      return nameInputError;
   }

   public void setNameInputError(CharSequence cardNameInputError) {
      this.nameInputError = cardNameInputError;
      isChanged = true;
      notifyPropertyChanged(BR.nameInputError);
   }

   public CommonCardViewModel getRecordModel() {
      return commonCardViewModel;
   }

   public boolean isChanged() {
      return isChanged;
   }

   public void setChanged(boolean changed) {
      isChanged = changed;
   }

   public boolean isErrorShown() {
      return nameInputError.length() > 0;
   }
   //// TODO: 7/30/17 move to common class
   @BindingAdapter("errorText")
   public static void setErrorMessage(TextInputLayout view, CharSequence errorMessage) {
      view.setError(errorMessage);
   }
}
