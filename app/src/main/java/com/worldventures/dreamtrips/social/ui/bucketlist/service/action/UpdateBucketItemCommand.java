package com.worldventures.dreamtrips.social.ui.bucketlist.service.action;

import com.worldventures.core.service.command.api_action.MappableApiActionCommand;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.bucketlist.UpdateBucketItemHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketUpdateBody;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.BucketBody;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class UpdateBucketItemCommand extends MappableApiActionCommand<UpdateBucketItemHttpAction, BucketItem, BucketItem> {

   private final BucketBody bucketBody;

   public UpdateBucketItemCommand(BucketBody bucketBody) {
      this.bucketBody = bucketBody;
   }

   @Override
   protected Class<BucketItem> getMappingTargetClass() {
      return BucketItem.class;
   }

   @Override
   protected Object mapHttpActionResult(UpdateBucketItemHttpAction httpAction) {
      return httpAction.response();
   }

   @Override
   protected UpdateBucketItemHttpAction getHttpAction() {
      return new UpdateBucketItemHttpAction(bucketBody.id(), mapperyContext.convert(bucketBody, BucketUpdateBody.class));
   }

   @Override
   protected Class<UpdateBucketItemHttpAction> getHttpActionClass() {
      return UpdateBucketItemHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.bucket_list_action_create_error;
   }

   public String getBucketItemId() {
      return bucketBody.id();
   }
}
