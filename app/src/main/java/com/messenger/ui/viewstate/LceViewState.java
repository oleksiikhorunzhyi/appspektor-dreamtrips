package com.messenger.ui.viewstate;

import android.os.Parcel;

public class LceViewState<C> extends BaseRestorableViewState {

   public enum LoadingState {
      LOADING,
      CONTENT,
      ERROR
   }

   private LoadingState loadingState = LoadingState.LOADING;
   private Throwable error;
   private C data;

   public LceViewState() {
   }

   public LoadingState getLoadingState() {
      return loadingState;
   }

   public void setLoadingState(LoadingState loadingState) {
      this.loadingState = loadingState;
   }

   public Throwable getError() {
      return error;
   }

   public void setError(Throwable error) {
      this.error = error;
   }

   public C getData() {
      return data;
   }

   public void setData(C data) {
      this.data = data;
   }

   @Override
   public void writeToParcel(Parcel parcel, int flags) {
      parcel.writeInt(loadingState.ordinal());
      parcel.writeSerializable(error);
   }

   public LceViewState(Parcel in) {
      loadingState = LoadingState.values()[in.readInt()];
      error = (Throwable) in.readSerializable();
   }
}
