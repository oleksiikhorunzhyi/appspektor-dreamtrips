package com.worldventures.dreamtrips.social.ui.tripsimages.service.command;


import android.graphics.BitmapFactory;
import android.net.Uri;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.modules.picker.command.CopyFileCommand;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.utils.Size;
import com.worldventures.core.utils.ValidationUtils;
import com.worldventures.core.modules.picker.util.CapturedRowMediaHelper;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoCreationItem;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CreatePhotoCreationItemCommand extends Command<PhotoCreationItem> implements InjectableAction {

   @Inject MediaPickerInteractor mediaInteractor;
   @Inject CapturedRowMediaHelper capturedRowMediaHelper;

   private PhotoPickerModel photoPickerModel;
   private MediaPickerAttachment.Source source;

   public CreatePhotoCreationItemCommand(PhotoPickerModel photoPickerModel, MediaPickerAttachment.Source source) {
      this.photoPickerModel = photoPickerModel;
      this.source = source;
   }

   @Override
   protected void run(CommandCallback<PhotoCreationItem> callback) throws Throwable {
      String fileUri = photoPickerModel.getUri().toString();
      if (ValidationUtils.isUrl(fileUri)) {
         mediaInteractor.copyFilePipe()
               .createObservableResult(new CopyFileCommand(fileUri))
               .subscribe(command -> {
                  String stringUri = command.getResult();
                  Uri uri = Uri.parse(stringUri);
                  callback.onSuccess(createPhotoItem(stringUri, uri.getPath(), getImageSize(uri.getPath())));
               }, callback::onFail);
      } else {
         PhotoCreationItem photoCreation = createPhotoItem(photoPickerModel.getUri()
                     .toString(), photoPickerModel.getAbsolutePath(),
               getImageSize(photoPickerModel.getUri().getPath()));
         photoCreation.setRotation(capturedRowMediaHelper.obtainPhotoOrientation(photoPickerModel.getUri().getPath()));
         callback.onSuccess(photoCreation);
      }
   }

   private PhotoCreationItem createPhotoItem(String uri, String path, Size size) {
      PhotoCreationItem item = new PhotoCreationItem();
      item.setId(photoPickerModel.getUri().hashCode());
      item.setSource(source);
      item.setCanDelete(true);
      item.setCanEdit(true);
      item.setFilePath(path);
      item.setFileUri(uri);
      item.setWidth(size.getWidth());
      item.setHeight(size.getHeight());
      return item;
   }

   private Size getImageSize(String path) {
      Size imageSize = photoPickerModel.getSize();
      if (imageSize == null) {
         BitmapFactory.Options options = new BitmapFactory.Options();
         options.inJustDecodeBounds = true;
         BitmapFactory.decodeFile(new File(path).getAbsolutePath(), options);
         return new Size(options.outWidth, options.outHeight);
      } else {
         return imageSize;
      }
   }
}
