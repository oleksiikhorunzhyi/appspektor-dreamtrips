package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesArgs;

import java.util.Date;
import java.util.List;

public class TripImagesCommandFactory {

   public BaseTripImagesCommand provideCommandCacheOnly(TripImagesArgs tripImagesArgs) {
      BaseTripImagesCommand command;

      if (tripImagesArgs.getRoute() == Route.MEMBERS_IMAGES) {
         command = new MemberImagesCommand(tripImagesArgs, true);
      } else {
         command = new UserImagesCommand(tripImagesArgs, true);
      }
      return command;
   }

   public BaseTripImagesCommand provideCommand(TripImagesArgs tripImagesArgs) {
      BaseTripImagesCommand command;

      if (tripImagesArgs.getRoute() == Route.MEMBERS_IMAGES) {
         command = new MemberImagesCommand(tripImagesArgs, ImmutablePaginationParams.builder()
               .before(new Date())
               .perPage(tripImagesArgs.getPageSize())
               .build());
      } else {
         command = new UserImagesCommand(tripImagesArgs, 1);
      }
      command.setReload(true);
      return command;
   }

   public BaseTripImagesCommand provideLoadMoreCommand(TripImagesArgs tripImagesArgs, List<BaseMediaEntity> mediaEntityList) {
      BaseTripImagesCommand command;
      if (tripImagesArgs.getRoute() == Route.MEMBERS_IMAGES) {
         command = new MemberImagesCommand(tripImagesArgs, ImmutablePaginationParams.builder()
               .before(mediaEntityList.get(mediaEntityList.size() - 1).getCreatedAt())
               .perPage(tripImagesArgs.getPageSize())
               .build());
      } else {
         int page = (mediaEntityList.size() / tripImagesArgs.getPageSize()) + 1;
         command = new UserImagesCommand(tripImagesArgs, page);
      }
      command.setLoadMore(true);
      return command;
   }

   public BaseTripImagesCommand provideRefreshCommand(TripImagesArgs tripImagesArgs, BaseMediaEntity newestMedia) {
      return new MemberImagesCommand(tripImagesArgs, ImmutablePaginationParams.builder()
            .after(newestMedia.getCreatedAt())
            .perPage(tripImagesArgs.getPageSize())
            .build());
   }

}
