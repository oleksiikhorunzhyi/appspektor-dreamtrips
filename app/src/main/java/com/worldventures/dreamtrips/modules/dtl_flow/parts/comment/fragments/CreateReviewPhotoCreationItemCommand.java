package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments;


import android.content.Context;
import android.support.v4.util.Pair;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.utils.ImageUtils;
import com.worldventures.core.utils.Size;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.FaceRecognitionUtils;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class CreateReviewPhotoCreationItemCommand extends Command<PhotoReviewCreationItem> implements InjectableAction {

   @Inject Context context;

   private PhotoPickerModel photoGalleryModel;

   public CreateReviewPhotoCreationItemCommand(PhotoPickerModel photoGalleryModel) {
      this.photoGalleryModel = photoGalleryModel;
   }

   @Override
   protected void run(CommandCallback<PhotoReviewCreationItem> callback) throws Throwable {
      ImageUtils.getBitmap(context, photoGalleryModel.getUri(), 300, 300)
            .compose(bitmapObservable -> Observable.zip(FaceRecognitionUtils.getRecognizedFaces(context, bitmapObservable),
                  bitmapObservable, Pair::new))
            .map(pair -> {
               PhotoReviewCreationItem item = new PhotoReviewCreationItem();
               item.setId(photoGalleryModel.getUri().hashCode());
               item.setFileUri(photoGalleryModel.getUri().toString());
               item.setFilePath(photoGalleryModel.getAbsolutePath());
               Size imageSize = photoGalleryModel.getSize();
               item.setWidth(imageSize != null ? imageSize.getWidth() : pair.second.getWidth());
               item.setHeight(imageSize != null ? imageSize.getHeight() : pair.second.getHeight());
               item.setSuggestions(pair.first);
               item.setSource(photoGalleryModel.getSource());
               item.setCanDelete(true);
               item.setCanEdit(true);
               return item;
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
