package com.worldventures.dreamtrips.modules.media_picker.service.command;

import android.content.Context;
import android.net.Uri;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.utils.ImageUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.FaceRecognitionUtils;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoCreationItem;

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
      FaceRecognitionUtils.getRecognizedFaces(context, ImageUtils.getBitmap(context, Uri.parse(photoCreationItem.getFileUri()), 300, 300)
      ).subscribe(photoTags -> {
         photoCreationItem.setSuggestions(photoTags);
         callback.onSuccess(photoCreationItem);
      }, callback::onFail);

   }
}
