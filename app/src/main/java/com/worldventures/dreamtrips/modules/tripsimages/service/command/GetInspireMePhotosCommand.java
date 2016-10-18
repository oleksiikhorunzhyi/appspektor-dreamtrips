package com.worldventures.dreamtrips.modules.tripsimages.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.inspirations.GetInspireMePhotosHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.TripImagesCommand;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetInspireMePhotosCommand extends TripImagesCommand<Inspiration> {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mappery;

   private double randomSeed; // used later when API lib is fixed for inspire me photos

   public GetInspireMePhotosCommand(double randomSeed, int page, int perPage) {
      super(page, perPage);
      this.randomSeed = randomSeed;
   }

   @Override
   protected void run(CommandCallback<List<Inspiration>> callback) throws Throwable {
      janet.createPipe(GetInspireMePhotosHttpAction.class)
            .createObservableResult(new GetInspireMePhotosHttpAction(randomSeed, page, perPage))
            .map(GetInspireMePhotosHttpAction::response)
            .map(inspireMePhotos -> mappery.convert(inspireMePhotos, Inspiration.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_inspire_images;
   }
}
