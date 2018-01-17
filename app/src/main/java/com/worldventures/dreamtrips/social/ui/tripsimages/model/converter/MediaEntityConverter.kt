package com.worldventures.dreamtrips.social.ui.tripsimages.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder
import com.worldventures.dreamtrips.api.entity.model.EntityHolder
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo
import com.worldventures.dreamtrips.social.ui.tripsimages.model.PhotoMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.model.MediaEntityType
import com.worldventures.dreamtrips.social.ui.tripsimages.model.UndefinedMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.model.VideoMediaEntity
import io.techery.mappery.MapperyContext

class MediaEntityConverter : Converter<EntityHolder<*>, BaseMediaEntity<*>> {

   override fun sourceClass() = EntityHolder::class.java

   override fun targetClass() = BaseMediaEntity::class.java

   override fun convert(mapperyContext: MapperyContext, entityHolder: EntityHolder<*>): BaseMediaEntity<*> {
      return when (fromApiType(entityHolder.type())) {
         MediaEntityType.PHOTO -> {
            val photo = entityHolder.entity()?.let { mapperyContext.convert(it, Photo::class.java) }
                  ?: throw Exception("Photo cannot be nullable")
            PhotoMediaEntity(photo)
         }
         MediaEntityType.VIDEO -> {
            val video = entityHolder.entity()?.let { mapperyContext.convert(it, Video::class.java) }
                  ?: throw Exception("Video cannot be nullable")
            VideoMediaEntity(video)
         }
         else -> UndefinedMediaEntity()
      }
   }

   private fun fromApiType(type: BaseEntityHolder.Type): MediaEntityType {
      return when (type) {
         BaseEntityHolder.Type.PHOTO -> MediaEntityType.PHOTO
         BaseEntityHolder.Type.VIDEO -> MediaEntityType.VIDEO
         else -> MediaEntityType.UNKNOWN
      }
   }
}
