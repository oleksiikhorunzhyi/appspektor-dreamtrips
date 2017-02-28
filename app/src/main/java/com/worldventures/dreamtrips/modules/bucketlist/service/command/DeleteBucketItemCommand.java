package com.worldventures.dreamtrips.modules.bucketlist.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.bucketlist.DeleteBucketItemHttpAction;
import com.worldventures.dreamtrips.core.api.action.ApiActionCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeleteBucketItemCommand extends ApiActionCommand<DeleteBucketItemHttpAction, FeedEntity> {

   private BucketItem bucketItem;

   public DeleteBucketItemCommand(BucketItem bucketItem) {
      this.bucketItem = bucketItem;
   }

   @Override
   protected FeedEntity mapHttpActionResult(DeleteBucketItemHttpAction httpAction) {
      return bucketItem;
   }

   @Override
   protected DeleteBucketItemHttpAction getHttpAction() {
      return new DeleteBucketItemHttpAction(bucketItem.getUid());
   }

   @Override
   protected Class<DeleteBucketItemHttpAction> getHttpActionClass() {
      return DeleteBucketItemHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.bucket_list_action_delete_error;
   }
}