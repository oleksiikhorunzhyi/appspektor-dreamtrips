package com.worldventures.dreamtrips.modules.reptools.model.converter;


import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;

import io.techery.mappery.MapperyContext;

public class SuccessStoryConverter implements Converter<com.worldventures.dreamtrips.api.success_stories.model.SuccessStory, SuccessStory> {

   @Override
   public Class<com.worldventures.dreamtrips.api.success_stories.model.SuccessStory> sourceClass() {
      return com.worldventures.dreamtrips.api.success_stories.model.SuccessStory.class;
   }

   @Override
   public Class<SuccessStory> targetClass() {
      return SuccessStory.class;
   }

   @Override
   public SuccessStory convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.success_stories.model.SuccessStory apiSuccessStory) {
      SuccessStory successStory = new SuccessStory();
      successStory.setId(apiSuccessStory.id());
      successStory.setAuthor(apiSuccessStory.author());
      successStory.setCategory(apiSuccessStory.category());
      successStory.setLocale(apiSuccessStory.locale());
      successStory.setUrl(apiSuccessStory.url());
      successStory.setSharingUrl(apiSuccessStory.sharingUrl());
      successStory.setLiked(apiSuccessStory.liked());
      return successStory;
   }
}
