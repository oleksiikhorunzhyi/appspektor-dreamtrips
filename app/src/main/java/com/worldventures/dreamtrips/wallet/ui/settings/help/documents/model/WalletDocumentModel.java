package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder.HelpDocsTypeFactory;

public class WalletDocumentModel extends BaseViewModel<HelpDocsTypeFactory> implements Parcelable {

   private String name;
   private String originalName;
   private String url;

   public WalletDocumentModel() {}

   public WalletDocumentModel(Document document) {
      this(document.getName(), document.getOriginalName(), document.getUrl());
   }

   public WalletDocumentModel(String name, String originalName, String url) {
      this.name = name;
      this.originalName = originalName;
      this.url = url;
   }

   protected WalletDocumentModel(Parcel in) {
      this.name = in.readString();
      this.originalName = in.readString();
      this.url = in.readString();
   }

   public String getName() {
      return name;
   }

   public String getOriginalName() {
      return originalName;
   }

   public String getUrl() {
      return url;
   }

   @Override
   public int type(HelpDocsTypeFactory typeFactory) {
      return typeFactory.type(this);
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.name);
      dest.writeString(this.originalName);
      dest.writeString(this.url);
   }

   public static final Creator<WalletDocumentModel> CREATOR = new Creator<WalletDocumentModel>() {
      @Override
      public WalletDocumentModel createFromParcel(Parcel source) {return new WalletDocumentModel(source);}

      @Override
      public WalletDocumentModel[] newArray(int size) {return new WalletDocumentModel[size];}
   };
}
