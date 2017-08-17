package com.worldventures.dreamtrips.modules.media_picker.service.command;

import android.content.Context;
import android.net.Uri;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.tripsimages.view.ImageUtils;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RecognizeFacesCommand extends Command<PhotoCreationItem> implements InjectableAction{

   private PhotoCreationItem photoCreationItem;

   @ForApplication @Inject Context context;

   public RecognizeFacesCommand(PhotoCreationItem photoCreationItem) {
      this.photoCreationItem = photoCreationItem;
   }

   @Override
   protected void run(CommandCallback<PhotoCreationItem> callback) throws Throwable {
      ImageUtils.getRecognizedFaces(context, ImageUtils.getBitmap(context, Uri.parse(photoCreationItem.getFileUri()), 300, 300)
      ).subscribe(photoTags -> {
         photoCreationItem.setSuggestions(photoTags);
         callback.onSuccess(photoCreationItem);
      }, callback::onFail);

   }
}
