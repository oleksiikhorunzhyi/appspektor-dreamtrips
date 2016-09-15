package com.worldventures.dreamtrips.modules.common.view.adapter.item;

import android.os.Parcel;
import android.os.Parcelable;

public class SelectableHeaderItem implements Parcelable {

   private String headerCaption = "";
   private boolean selected;

   public SelectableHeaderItem(String headerCaption, boolean isSelected) {
      this.headerCaption = headerCaption;
      this.selected = isSelected;
   }

   public String getHeaderCaption() {
      return headerCaption;
   }

   public void setHeaderCaption(String headerCaption) {
      this.headerCaption = headerCaption;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   protected SelectableHeaderItem(Parcel in) {
      headerCaption = in.readString();
      selected = in.readByte() != 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(headerCaption);
      dest.writeByte((byte) (selected ? 1 : 0));
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<SelectableHeaderItem> CREATOR = new Creator<SelectableHeaderItem>() {
      @Override
      public SelectableHeaderItem createFromParcel(Parcel in) {
         return new SelectableHeaderItem(in);
      }

      @Override
      public SelectableHeaderItem[] newArray(int size) {
         return new SelectableHeaderItem[size];
      }
   };

   ///////////////////////////////////////////////////////////////////////////
   // java.lang.Object-overridden
   ///////////////////////////////////////////////////////////////////////////


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SelectableHeaderItem that = (SelectableHeaderItem) o;

      if (selected != that.selected) return false;
      if (!headerCaption.equals(that.headerCaption)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = headerCaption.hashCode();
      result = 31 * result + (selected ? 1 : 0);
      return result;
   }

   @Override
   public String toString() {
      return "SelectableHeaderItem{" +
            "headerCaption='" + headerCaption + '\'' +
            ", selected=" + selected +
            '}';
   }
}
