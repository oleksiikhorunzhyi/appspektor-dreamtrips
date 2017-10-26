package com.worldventures.wallet.service.beacon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class RegionBundle {

   @NonNull
   private final String name;
   @NonNull
   private final String uuid;
   @Nullable
   private final String minor;
   @Nullable
   private final String major;

   public RegionBundle(@NonNull String name, @NonNull String uuid, @Nullable String minor, @Nullable String major) {
      this.name = name;
      this.uuid = uuid;
      this.minor = minor;
      this.major = major;
   }

   @NonNull
   public String getName() {
      return name;
   }

   @NonNull
   public String getUuid() {
      return uuid;
   }

   @Nullable
   public String getMinor() {
      return minor;
   }

   @Nullable
   public String getMajor() {
      return major;
   }

   @Override
   public String toString() {
      return "RegionBundle{" +
            "name='" + name + '\'' +
            ", uuid='" + uuid + '\'' +
            ", minor='" + minor + '\'' +
            ", major='" + major + '\'' +
            '}';
   }
}
