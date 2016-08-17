package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BucketTag implements Serializable {

   private String name;

   public String getName() {
      return name;
   }
}
