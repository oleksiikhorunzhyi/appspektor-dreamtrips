package com.worldventures.dreamtrips.core.component;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

public class ComponentDescription implements Parcelable {
   private final String key;
   private final int toolbarTitle;
   private final int navMenuTitle;
   private final int icon;
   private final boolean ignored;
   private final Class<? extends Fragment> fragmentClass;
   private final boolean skipGeneralToolbar;

   public ComponentDescription(String key, @StringRes int toolbarTitle, @StringRes int navMenuTitle, @DrawableRes int iconRes, Class<? extends Fragment> fragmentClass) {
      this(key, toolbarTitle, navMenuTitle, iconRes, false, false, fragmentClass);
   }

   public ComponentDescription(String key, @StringRes int toolbarTitle, @StringRes int navMenuTitle, @DrawableRes int icon, boolean ignored, boolean skipGeneralToolbar, Class<? extends Fragment> fragmentClass) {
      this.key = key;
      this.toolbarTitle = toolbarTitle;
      this.navMenuTitle = navMenuTitle;
      this.icon = icon;
      this.ignored = ignored;
      this.skipGeneralToolbar = skipGeneralToolbar;
      this.fragmentClass = fragmentClass;
   }

   public ComponentDescription(String key, @StringRes int toolbarTitle, @StringRes int navMenuTitle, @DrawableRes int iconRes, boolean skipGeneralToolbar, Class<? extends Fragment> fragmentClass) {
      this(key, toolbarTitle, navMenuTitle, iconRes, false, skipGeneralToolbar, fragmentClass);
   }

   public boolean isIgnored() {
      return ignored;
   }

   public boolean isSkipGeneralToolbar() {
      return skipGeneralToolbar;
   }

   public String getKey() {
      return key;
   }

   public int getToolbarTitle() {
      return toolbarTitle;
   }

   public int getNavMenuTitle() {
      return navMenuTitle;
   }

   public int getIcon() {
      return icon;
   }

   public Class<? extends Fragment> getFragmentClass() {
      return fragmentClass;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ComponentDescription that = (ComponentDescription) o;

      return !(key != null ? !key.equals(that.key) : that.key != null);

   }

   @Override
   public int hashCode() {
      return key != null ? key.hashCode() : 0;
   }


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.key);
      dest.writeInt(this.navMenuTitle);
      dest.writeInt(this.toolbarTitle);
      dest.writeInt(this.icon);
      dest.writeByte(ignored ? (byte) 1 : (byte) 0);
      dest.writeByte(skipGeneralToolbar ? (byte) 1 : (byte) 0);
      dest.writeSerializable(this.fragmentClass);
   }

   private ComponentDescription(Parcel in) {
      this.key = in.readString();
      this.navMenuTitle = in.readInt();
      this.toolbarTitle = in.readInt();
      this.icon = in.readInt();
      this.ignored = in.readByte() != 0;
      this.skipGeneralToolbar = in.readByte() != 0;
      this.fragmentClass = (Class<? extends Fragment>) in.readSerializable();
   }

   public static final Creator<ComponentDescription> CREATOR = new Creator<ComponentDescription>() {
      public ComponentDescription createFromParcel(Parcel source) {
         return new ComponentDescription(source);
      }

      public ComponentDescription[] newArray(int size) {
         return new ComponentDescription[size];
      }
   };
}
