package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public abstract class TimeBasedPaginatedTripImagesCommand<T extends IFullScreenObject> extends CommandWithError<List<T>>
      implements InjectableAction, CommandWithTripImages {

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   protected Date before;
   protected Date after;
   protected int perPage;

   public TimeBasedPaginatedTripImagesCommand(PaginationParams paginationParams) {
      this.before = paginationParams.before();
      this.after = paginationParams.after();
      this.perPage = paginationParams.perPage();
   }

   @Override
   public List<IFullScreenObject> getImages() {
      return new ArrayList<>(getResult());
   }

   @Value.Immutable
   public interface PaginationParams {

      int perPage();

      @Nullable
      Date before();

      @Nullable
      Date after();
   }
}
