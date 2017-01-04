package com.worldventures.dreamtrips.modules.bucketlist.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.bucketlist.DeleteBucketItemHttpAction;
import com.worldventures.dreamtrips.core.api.action.ApiActionCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeleteBucketItemCommand extends ApiActionCommand<DeleteBucketItemHttpAction, Object> {
   String uid;

   public DeleteBucketItemCommand(String uid) {
      this.uid = uid;
   }

   public String getBucketItemUid() {
      return uid;
   }

   @Override
   protected DeleteBucketItemHttpAction getHttpAction() {
      return new DeleteBucketItemHttpAction(uid);
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