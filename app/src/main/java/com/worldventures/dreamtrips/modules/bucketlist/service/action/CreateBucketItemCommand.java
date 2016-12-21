package com.worldventures.dreamtrips.modules.bucketlist.service.action;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.bucketlist.CreateBucketItemHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketCreationBody;
import com.worldventures.dreamtrips.core.api.action.MappableApiActionCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketBody;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CreateBucketItemCommand extends MappableApiActionCommand<CreateBucketItemHttpAction, BucketItem, BucketItem> {
   private BucketBody bucketBody;

   public CreateBucketItemCommand(BucketBody bucketBody) {
      this.bucketBody = bucketBody;
   }

   @Override
   protected Class<BucketItem> getMappingTargetClass() {
      return BucketItem.class;
   }

   @Override
   protected Object mapHttpActionResult(CreateBucketItemHttpAction httpAction) {
      return httpAction.response();
   }

   @Override
   protected CreateBucketItemHttpAction getHttpAction() {
      return new CreateBucketItemHttpAction(mapperyContext.convert(bucketBody, BucketCreationBody.class));
   }

   @Override
   protected Class<CreateBucketItemHttpAction> getHttpActionClass() {
      return CreateBucketItemHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.bucket_list_action_create_error;
   }
}