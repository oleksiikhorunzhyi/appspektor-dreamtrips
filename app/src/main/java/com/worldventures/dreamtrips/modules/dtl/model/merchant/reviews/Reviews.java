package com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;
import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface Reviews extends Serializable {

   String lastModeratedTimeUtc();
   String reviewId();
   String brand();
   String userNickName();
   String userImage();
   String reviewText();
   Integer rating();
   Boolean verified();
}
