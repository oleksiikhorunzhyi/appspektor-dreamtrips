package com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews;

import org.immutables.value.Value;
import java.io.Serializable;

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
