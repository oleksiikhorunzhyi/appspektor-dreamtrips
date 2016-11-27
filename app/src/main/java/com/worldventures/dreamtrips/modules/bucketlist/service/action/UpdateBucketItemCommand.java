package com.worldventures.dreamtrips.modules.bucketlist.service.action;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.bucketlist.CreateBucketItemHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.UpdateBucketItemHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketCreationBody;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketUpdateBody;
import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.core.api.action.MappableApiActionCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.BucketBody;

import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

@CommandAction
public class UpdateBucketItemCommand extends MappableApiActionCommand<UpdateBucketItemHttpAction, BucketItem, BucketItem> {

   private BucketBody bucketBody;

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
