package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public abstract class PaginatedTripImagesCommand<T extends IFullScreenObject> extends CommandWithError<List<T>>
      implements InjectableAction, CommandWithTripImages {

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   protected int perPage;
   protected int page;

   public PaginatedTripImagesCommand(int page, int perPage) {
      this.page = page;
      this.perPage = perPage;
   }

   public void setPage(int page) {
      this.page = page;
   }

   public void setPerPage(int perPage) {
      this.perPage = perPage;
   }

   @Override
   public List<IFullScreenObject> getImages() {
      return new ArrayList<>(getResult());
   }
}
