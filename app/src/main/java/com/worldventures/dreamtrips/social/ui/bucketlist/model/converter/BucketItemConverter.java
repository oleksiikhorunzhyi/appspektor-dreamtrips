package com.worldventures.dreamtrips.social.ui.bucketlist.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketLocation;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketTag;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.DiningItem;

import java.util.ArrayList;

import io.techery.mappery.MapperyContext;

public abstract class BucketItemConverter<T extends com.worldventures.dreamtrips.api.bucketlist.model.BucketItem>
      implements Converter<T, BucketItem> {

   @Override
   public Class<BucketItem> targetClass() {
      return BucketItem.class;
   }

   @Override
   public BucketItem convert(MapperyContext mapperyContext, T apiBucketItem) {
      BucketItem bucketItem = new BucketItem();
      bucketItem.setUid(apiBucketItem.uid());
      bucketItem.setName(apiBucketItem.name());
      bucketItem.setDescription(apiBucketItem.description());
      if (apiBucketItem.category() != null) {
         bucketItem.setCategory(mapperyContext.convert(apiBucketItem.category(), CategoryItem.class));
      }

      bucketItem.setType(mapperyContext.convert(apiBucketItem.type(), BucketItem.BucketType.class)
            .toString()
            .toLowerCase());
      bucketItem.setStatus(apiBucketItem.status().toString().toLowerCase());

      if (apiBucketItem.location() != null) {
         bucketItem.setLocation(mapperyContext.convert(apiBucketItem.location(), BucketLocation.class));
      }

      bucketItem.setTargetDate(apiBucketItem.targetDate());
      bucketItem.setCompletionDate(apiBucketItem.completionDate());

      if (apiBucketItem.bucketCoverPhoto() != null) {
         bucketItem.setCoverPhoto(mapperyContext.convert(apiBucketItem.bucketCoverPhoto(), BucketPhoto.class));
      }

      bucketItem.setPhotos(mapperyContext.convert(apiBucketItem.bucketPhoto(), BucketPhoto.class));

      bucketItem.setLink(apiBucketItem.link());

      if (apiBucketItem.tags() != null) {
         bucketItem.setTags(mapperyContext.convert(apiBucketItem.tags(), BucketTag.class));
      }

      bucketItem.setFriends(new ArrayList<>(apiBucketItem.friends()));

      bucketItem.setLanguage(apiBucketItem.language());

      if (apiBucketItem.dining() != null) {
         bucketItem.setDining(mapperyContext.convert(apiBucketItem.dining(), DiningItem.class));
      }

      return bucketItem;
   }
}
