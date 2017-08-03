package com.worldventures.dreamtrips.wallet.ui.settings.common.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.holder.DefaultCardHolderTypeFactory;

public class SectionDividerModel extends BaseViewModel<DefaultCardHolderTypeFactory> implements Parcelable {

   private @StringRes int titleId;

   public SectionDividerModel(@StringRes int titleId) {
      this.titleId = titleId;
   }

   public int getTitleId() {
      return titleId;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {dest.writeInt(this.titleId);}

   protected SectionDividerModel(Parcel in) {this.titleId = in.readInt();}

   public static final Creator<SectionDividerModel> CREATOR = new Creator<SectionDividerModel>() {
      @Override
      public SectionDividerModel createFromParcel(Parcel source) {return new SectionDividerModel(source);}

      @Override
      public SectionDividerModel[] newArray(int size) {return new SectionDividerModel[size];}
   };

   @Override
   public int type(DefaultCardHolderTypeFactory typeFactory) {
      return typeFactory.type(this);
   }
}
