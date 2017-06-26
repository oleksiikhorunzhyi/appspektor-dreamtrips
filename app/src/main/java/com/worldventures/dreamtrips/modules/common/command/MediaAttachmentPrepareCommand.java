package com.worldventures.dreamtrips.modules.common.command;

import android.net.Uri;
import android.util.Pair;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class MediaAttachmentPrepareCommand extends Command<List<Uri>> implements InjectableAction {
   private final List<BasePickerViewModel> attachmentToPrepare;
   @Inject DrawableUtil drawableUtil;

   public MediaAttachmentPrepareCommand(List<BasePickerViewModel> attachmentToPrepare) {
      this.attachmentToPrepare = attachmentToPrepare;
   }

   @Override
   protected void run(CommandCallback<List<Uri>> callback) throws Throwable {
      try {
         final List<Uri> result = new ArrayList<>();
         for (BasePickerViewModel model : attachmentToPrepare) {
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
