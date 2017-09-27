package com.worldventures.dreamtrips.modules.picker.command;

import android.net.Uri;
import android.util.Pair;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.social.util.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class MediaAttachmentPrepareCommand extends Command<List<Uri>> implements InjectableAction {
   private final List<PhotoPickerModel> attachmentToPrepare;
   @Inject DrawableUtil drawableUtil;

   public MediaAttachmentPrepareCommand(List<PhotoPickerModel> attachmentToPrepare) {
      this.attachmentToPrepare = attachmentToPrepare;
   }

   @Override
   protected void run(CommandCallback<List<Uri>> callback) throws Throwable {
      try {
         final List<Uri> result = new ArrayList<>();
         for (PhotoPickerModel model : attachmentToPrepare) {
            Pair<String, Size> pair = ImageUtils.generateUri(drawableUtil, model.getAbsolutePath());
            result.add(convertPickedPhotoToUri(pair.first));

         }
         callback.onSuccess(result);
      } catch (Throwable throwable) {
         callback.onFail(throwable);
      }
   }

   private Uri convertPickedPhotoToUri(String path) {
      Uri uri = Uri.parse(path);
      if (uri.getScheme() == null) {
         //check if is local file path
         final File localFile = new File(path);
         if (localFile.exists()) {
            uri = Uri.fromFile(localFile);
         } else {
            throw new IllegalStateException("Cannot parse path into Uri : " + path);
         }
      }
      return uri;
   }
}
