package com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews;

import android.support.annotation.Nullable;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface Review extends Serializable {

   @Nullable String lastModeratedTimeUtc();
   @Nullable String reviewId();
   @Nullable Integer brand();
   @Nullable String userNickName();
   @Nullable UserImage userImage();
   @Nullable String reviewText();
   @Nullable Integer rating();
   @Nullable Boolean verified();
   @Nullable List<Errors> errors();
   @Nullable List<ReviewImages> reviewImagesList();
}
