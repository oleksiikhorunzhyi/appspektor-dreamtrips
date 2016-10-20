package com.worldventures.dreamtrips.modules.bucketlist.model.converter;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSocialized;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import io.techery.mappery.MapperyContext;

public class BucketItemSocializedConverter extends BucketItemConverter<BucketItemSocialized> {

   @Override
   public Class<BucketItemSocialized> sourceClass() {
      return BucketItemSocialized.class;
   }

   @Override
   public BucketItem convert(MapperyContext mapperyContext, BucketItemSocialized apiBucketItem) {
      BucketItem bucketItem = super.convert(mapperyContext, apiBucketItem);

      bucketItem.setLiked(apiBucketItem.liked());
      bucketItem.setLikesCount(apiBucketItem.likes());

      bucketItem.setCommentsCount(apiBucketItem.commentsCount());
      bucketItem.setComments(mapperyContext.convert(apiBucketItem.comments(), Comment.class));

      return bucketItem;
   }
}
