package com.worldventures.wallet.ui.settings.help.documents.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.wallet.ui.settings.help.documents.holder.HelpDocsTypeFactory;

public class WalletLoadMoreModel extends BaseViewModel<HelpDocsTypeFactory> implements Parcelable {

   private boolean loading;
   private boolean visible;

   public boolean isLoading() {
      return loading;
   }

   public boolean isVisible() {
      return visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public void setLoading(boolean loading) {
      this.loading = loading;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeByte(this.loading ? (byte) 1 : (byte) 0);
      dest.writeByte(this.visible ? (byte) 1 : (byte) 0);
   }

   public WalletLoadMoreModel() {}

   protected WalletLoadMoreModel(Parcel in) {
      this.loading = in.readByte() != 0;
      this.visible = in.readByte() != 0;
   }

   public static final Creator<WalletLoadMoreModel> CREATOR = new Creator<WalletLoadMoreModel>() {
      @Override
      public WalletLoadMoreModel createFromParcel(Parcel source) {
         return new WalletLoadMoreModel(source);
      }

      @Override
      public WalletLoadMoreModel[] newArray(int size) {
         return new WalletLoadMoreModel[size];
      }
   };

   @Override
   public int type(HelpDocsTypeFactory typeFactory) {
      return typeFactory.type(this);
   }
}
