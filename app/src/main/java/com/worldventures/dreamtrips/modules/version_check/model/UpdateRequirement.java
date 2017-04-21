package com.worldventures.dreamtrips.modules.version_check.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class UpdateRequirement {

   private String appVersion;
   private long timeStamp;

   public UpdateRequirement() {
   }

   public UpdateRequirement(String appVersion, long timeStamp) {
      this.appVersion = appVersion;
      this.timeStamp = timeStamp;
   }

   public long getTimeStamp() {
      return timeStamp;
   }

   public String getAppVersion() {
      return appVersion;
   }

   @Override
   public String toString() {
      return String.format("Suggested version info, app version %s, timestamp %d", appVersion, timeStamp);
   }
}
