package com.worldventures.dreamtrips.modules.bucketlist.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.bucketlist.UpdateBucketItemPositionHttpAction;
import com.worldventures.dreamtrips.core.api.action.ApiActionCommand;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ChangeBucketListOrderCommand extends ApiActionCommand<UpdateBucketItemPositionHttpAction, Object> {

   private String uid;
   private int position;

   public ChangeBucketListOrderCommand(String uid, int position) {
      this.uid = uid;
      this.position = position;
   }

   @Override
   protected UpdateBucketItemPositionHttpAction getHttpAction() {
      return new UpdateBucketItemPositionHttpAction(uid, position);
   }

   @Override
   protected Class<UpdateBucketItemPositionHttpAction> getHttpActionClass() {
      return UpdateBucketItemPositionHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.bucket_list_action_change_order_error;
   }
}
