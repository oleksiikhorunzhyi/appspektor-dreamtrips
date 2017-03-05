package com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews;

import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;
import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface Review extends Serializable {

   String lastModeratedTimeUtc();
   String reviewId();
   String brand();
   String userNickName();
   @Nullable UserImage userImage();
   String reviewText();
   Integer rating();
   Boolean verified();
}
