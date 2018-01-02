package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs

import java.util.Date

class TripImagesCommandFactory {

   fun provideCommandCacheOnly(tripImagesArgs: TripImagesArgs): BaseMediaCommand {
      return if (tripImagesArgs.tripImageType == TripImagesArgs.TripImageType.MEMBER_IMAGES) {
         GetMemberMediaCommand(tripImagesArgs, true)
      } else {
         GetUsersMediaCommand(tripImagesArgs, true)
      }
   }

   fun provideCommand(tripImagesArgs: TripImagesArgs): BaseMediaCommand {
      val command: BaseMediaCommand = if (tripImagesArgs.tripImageType == TripImagesArgs.TripImageType.MEMBER_IMAGES) {
         GetMemberMediaCommand(tripImagesArgs, ImmutablePaginationParams.builder()
               .before(Date())
               .perPage(tripImagesArgs.pageSize)
               .build())
      } else {
         GetUsersMediaCommand(tripImagesArgs, ImmutablePaginationParams.builder()
               .before(Date())
               .perPage(tripImagesArgs.pageSize)
               .build())
      }
      command.isReload = true
      return command
   }

   fun provideLoadMoreCommand(tripImagesArgs: TripImagesArgs, mediaEntityList: List<BaseMediaEntity<*>>): BaseMediaCommand {
      val command: BaseMediaCommand = if (tripImagesArgs.tripImageType == TripImagesArgs.TripImageType.MEMBER_IMAGES) {
         GetMemberMediaCommand(tripImagesArgs, ImmutablePaginationParams.builder()
               .before(mediaEntityList[mediaEntityList.size - 1].item.createdAt)
               .perPage(tripImagesArgs.pageSize)
               .build())
      } else {
         GetUsersMediaCommand(tripImagesArgs, ImmutablePaginationParams.builder()
               .before(mediaEntityList[mediaEntityList.size - 1].item.createdAt)
               .perPage(tripImagesArgs.pageSize)
               .build())
      }
      command.isLoadMore = true
      return command
   }

   fun provideRefreshCommand(tripImagesArgs: TripImagesArgs, newestMedia: BaseMediaEntity<*>): BaseMediaCommand =
      GetMemberMediaCommand(tripImagesArgs, ImmutablePaginationParams.builder()
            .after(newestMedia.item.createdAt)
            .perPage(tripImagesArgs.pageSize)
            .build())
}
