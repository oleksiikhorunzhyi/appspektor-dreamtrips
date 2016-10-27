package com.worldventures.dreamtrips.modules.bucketlist.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListCategoriesHttpAction;
import com.worldventures.dreamtrips.core.api.action.MappableApiActionCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetCategoriesCommand extends MappableApiActionCommand<GetBucketListCategoriesHttpAction,
      List<CategoryItem>, CategoryItem> {

   @Override
   protected Class<CategoryItem> getMappingTargetClass() {
      return CategoryItem.class;
   }

   @Override
   protected Object mapHttpActionResult(GetBucketListCategoriesHttpAction httpAction) {
      return httpAction.response();
   }

   @Override
   protected GetBucketListCategoriesHttpAction getHttpAction() {
      return new GetBucketListCategoriesHttpAction();
   }

   @Override
   protected Class<GetBucketListCategoriesHttpAction> getHttpActionClass() {
      return GetBucketListCategoriesHttpAction.class;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_categories;
   }
}
