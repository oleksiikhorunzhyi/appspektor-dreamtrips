package com.worldventures.dreamtrips.core.component;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

public class ComponentDescription implements Parcelable{
   private final String key;
   private final int toolbarTitle;
   private final int toolbarLogo;
   private final int navMenuTitle;
   private final int icon;
   private final boolean ignored;
   private final Class<? extends Fragment> fragmentClass;
   private final boolean skipGeneralToolbar;
   private final boolean shouldFinishMainActivity;

   public ComponentDescription(Builder builder) {
      this.key = builder.key;
      this.toolbarTitle = builder.toolbarTitle;
      this.toolbarLogo = builder.toolbarLogo;
      this.navMenuTitle = builder.navMenuTitle;
      this.icon = builder.icon;
      this.ignored = builder.ignored;
      this.skipGeneralToolbar = builder.skipGeneralToolbar;
      this.fragmentClass = builder.fragmentClass;
      this.shouldFinishMainActivity = builder.shouldFinishMainActivity;
   }

   protected ComponentDescription(Parcel in) {
      key = in.readString();
      toolbarTitle = in.readInt();
      toolbarLogo = in.readInt();
      navMenuTitle = in.readInt();
      icon = in.readInt();
      ignored = in.readByte() != 0;
      fragmentClass = (Class<? extends Fragment>) in.readSerializable();
      skipGeneralToolbar = in.readByte() != 0;
      shouldFinishMainActivity = in.readByte() != 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(key);
      dest.writeInt(toolbarTitle);
      dest.writeInt(toolbarLogo);
      dest.writeInt(navMenuTitle);
      dest.writeInt(icon);
      dest.writeByte((byte) (ignored ? 1 : 0));
      dest.writeSerializable(fragmentClass);
      dest.writeByte((byte) (skipGeneralToolbar ? 1 : 0));
      dest.writeByte((byte) (shouldFinishMainActivity ? 1 : 0));
   }

   @Override
   public int describeContents() {
      return 0;
   }

   public static final Creator<ComponentDescription> CREATOR = new Creator<ComponentDescription>() {
      @Override
      public ComponentDescription createFromParcel(Parcel in) {
         return new ComponentDescription(in);
      }

      @Override
      public ComponentDescription[] newArray(int size) {
         return new ComponentDescription[size];
      }
   };

   public boolean isIgnored() {
      return ignored;
   }

   public boolean skipGeneralToolbar() {
      return skipGeneralToolbar;
   }

   public String getKey() {
      return key;
   }

   public int getToolbarTitle() {
      return toolbarTitle;
   }

   public int getToolbarLogo() {
      return toolbarLogo;
   }

   public int getNavMenuTitle() {
      return navMenuTitle;
   }

   public int getIcon() {
      return icon;
   }

   public boolean shouldFinishMainActivity() {
      return shouldFinishMainActivity;
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

   public static class Builder {
      private String key;
      private int toolbarTitle;
      private int icon;
      private int toolbarLogo;
      private boolean ignored;
      private Class<? extends Fragment> fragmentClass;
      private int navMenuTitle;
      private boolean skipGeneralToolbar;
      private boolean shouldFinishMainActivity;

      public Builder key(String key) {
         this.key = key;
         return this;
      }

      public Builder toolbarTitle(int toolbarTitle) {
         this.toolbarTitle = toolbarTitle;
         return this;
      }

      public Builder icon(int icon) {
         this.icon = icon;
         return this;
      }

      public Builder toolbarLogo(int toolbarLogo) {
         this.toolbarLogo = toolbarLogo;
         return this;
      }

      public Builder ignored(boolean ignored) {
         this.ignored = ignored;
         return this;
      }

      public Builder fragmentClass(Class<? extends Fragment> fragmentClass) {
         this.fragmentClass = fragmentClass;
         return this;
      }

      public Builder navMenuTitle(int navMenuTitle) {
         this.navMenuTitle = navMenuTitle;
         return this;
      }

      public Builder skipGeneralToolbar(boolean skipGeneralToolbar) {
         this.skipGeneralToolbar = skipGeneralToolbar;
         return this;
      }

      public Builder shouldFinishMainActivity(boolean shouldFinishMainActivity) {
         this.shouldFinishMainActivity = shouldFinishMainActivity;
         return this;
      }

      public ComponentDescription build() {
         return new ComponentDescription(this);
      }
   }
}
