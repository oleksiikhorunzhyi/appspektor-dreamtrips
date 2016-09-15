package com.worldventures.dreamtrips.modules.settings.dialog;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SelectDialogModel implements Parcelable {
   private int titleId;
   private ArrayList<String> items;
   private int selectedPosition;

   public SelectDialogModel() {
   }

   protected SelectDialogModel(Parcel in) {
      titleId = in.readInt();
      items = in.createStringArrayList();
      selectedPosition = in.readInt();
   }

   public int getTitleId() {
      return titleId;
   }

   public void setTitleId(int titleId) {
      this.titleId = titleId;
   }

   public ArrayList<String> getItems() {
      return items;
   }

   public void setItems(ArrayList<String> items) {
      this.items = items;
   }

   public int getSelectedPosition() {
      return selectedPosition;
   }

   public void setSelectedPosition(int selectedPosition) {
      this.selectedPosition = selectedPosition;
   }

   public static final Creator<SelectDialogModel> CREATOR = new Creator<SelectDialogModel>() {
      @Override
      public SelectDialogModel createFromParcel(Parcel in) {
         return new SelectDialogModel(in);
      }

      @Override
      public SelectDialogModel[] newArray(int size) {
         return new SelectDialogModel[size];
      }
   };

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(titleId);
      dest.writeStringList(items);
      dest.writeInt(selectedPosition);
   }
}
