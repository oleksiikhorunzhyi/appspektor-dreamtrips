package com.worldventures.dreamtrips.social.ui.reptools.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory

import io.techery.mappery.MapperyContext

typealias ApiSuccessStory = com.worldventures.dreamtrips.api.success_stories.model.SuccessStory

class SuccessStoryConverter : Converter<ApiSuccessStory, SuccessStory> {

   override fun sourceClass(): Class<ApiSuccessStory> = ApiSuccessStory::class.java

   override fun targetClass(): Class<SuccessStory> = SuccessStory::class.java

   override fun convert(context: MapperyContext, source: ApiSuccessStory) =
      SuccessStory(
            id = source.id(),
            author = source.author(),
            category = source.category(),
            locale = source.locale(),
            url = source.url(),
            sharingUrl = source.sharingUrl(),
            isLiked = source.liked()
      )
}
