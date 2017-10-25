package com.worldventures.dreamtrips.social.ui.bucketlist.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketCategory;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.CategoryItem;

import io.techery.mappery.MapperyContext;

public class BucketCategoryConverter implements Converter<BucketCategory, CategoryItem> {

   @Override
   public Class<BucketCategory> sourceClass() {
      return BucketCategory.class;
   }

   @Override
   public Class<CategoryItem> targetClass() {
      return CategoryItem.class;
   }

   @Override
   public CategoryItem convert(MapperyContext mapperyContext, BucketCategory bucketCategory) {
      return new CategoryItem(bucketCategory.id(), bucketCategory.name());
   }
}
