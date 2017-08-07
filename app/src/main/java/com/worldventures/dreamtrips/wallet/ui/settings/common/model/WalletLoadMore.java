package com.worldventures.dreamtrips.wallet.ui.settings.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder.HelpDocsTypeFactory;

public class WalletLoadMore extends BaseViewModel<HelpDocsTypeFactory> implements Parcelable {

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
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeByte(this.loading ? (byte) 1 : (byte) 0);
      dest.writeByte(this.visible ? (byte) 1 : (byte) 0);
   }

   public WalletLoadMore() {}

   protected WalletLoadMore(Parcel in) {
      this.loading = in.readByte() != 0;
      this.visible = in.readByte() != 0;
   }

   public static final Creator<WalletLoadMore> CREATOR = new Creator<WalletLoadMore>() {
      @Override
      public WalletLoadMore createFromParcel(Parcel source) {return new WalletLoadMore(source);}

      @Override
      public WalletLoadMore[] newArray(int size) {return new WalletLoadMore[size];}
   };

   @Override
   public int type(HelpDocsTypeFactory typeFactory) {
      return typeFactory.type(this);
   }
}
