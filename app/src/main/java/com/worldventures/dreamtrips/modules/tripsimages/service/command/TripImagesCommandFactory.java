package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesArgs;

import java.util.Date;
import java.util.List;

public class TripImagesCommandFactory {

   public BaseMediaCommand provideCommandCacheOnly(TripImagesArgs tripImagesArgs) {
      BaseMediaCommand command;
      if (tripImagesArgs.getRoute() == Route.MEMBERS_IMAGES) {
         command = new GetMemberMediaCommand(tripImagesArgs, true);
      } else {
         command = new GetUsersMediaCommand(tripImagesArgs, true);
      }
      return command;
   }

   public BaseMediaCommand provideCommand(TripImagesArgs tripImagesArgs) {
      BaseMediaCommand command;
      if (tripImagesArgs.getRoute() == Route.MEMBERS_IMAGES) {
         command = new GetMemberMediaCommand(tripImagesArgs, ImmutablePaginationParams.builder()
               .before(new Date())
               .perPage(tripImagesArgs.getPageSize())
               .build());
      } else {
         command = new GetUsersMediaCommand(tripImagesArgs, ImmutablePaginationParams.builder()
               .before(new Date())
               .perPage(tripImagesArgs.getPageSize())
               .build());
      }
      command.setReload(true);
      return command;
   }

   public BaseMediaCommand provideLoadMoreCommand(TripImagesArgs tripImagesArgs, List<BaseMediaEntity> mediaEntityList) {
      BaseMediaCommand command;
      if (tripImagesArgs.getRoute() == Route.MEMBERS_IMAGES) {
         command = new GetMemberMediaCommand(tripImagesArgs, ImmutablePaginationParams.builder()
               .before(mediaEntityList.get(mediaEntityList.size() - 1).getItem().getCreatedAt())
               .perPage(tripImagesArgs.getPageSize())
               .build());
      } else {
         command = new GetUsersMediaCommand(tripImagesArgs, ImmutablePaginationParams.builder()
               .before(mediaEntityList.get(mediaEntityList.size() - 1).getItem().getCreatedAt())
               .perPage(tripImagesArgs.getPageSize())
               .build());
      }
      command.setLoadMore(true);
      return command;
   }

   public BaseMediaCommand provideRefreshCommand(TripImagesArgs tripImagesArgs, BaseMediaEntity newestMedia) {
      return new GetMemberMediaCommand(tripImagesArgs, ImmutablePaginationParams.builder()
            .after(newestMedia.getItem().getCreatedAt())
            .perPage(tripImagesArgs.getPageSize())
            .build());
   }

}
