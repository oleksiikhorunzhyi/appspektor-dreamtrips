package com.worldventures.dreamtrips.modules.common.presenter;

import android.util.Pair;

import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.RxThreadFactory;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.worldventures.dreamtrips.modules.facebook.view.fragment.FacebookPhotoFragment.PHOTOS_TYPE_FACEBOOK;

public class MediaPickerPresenter extends Presenter<MediaPickerPresenter.View> {

   private static final String THREAD_NAME_PREFIX = "MEDIA_PICKER_THREAD";

   private int requestId;

   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject MediaInteractor mediaInteractor;
   @Inject DrawableUtil drawableUtil;

   public MediaPickerPresenter(int requestId) {
      this.requestId = requestId;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      mediaInteractor.imageCapturedPipe()
            .observeSuccess()
            .map(imageCapturedCommand -> {
               Pair<String, Size> pair = ImageUtils.generateUri(drawableUtil, imageCapturedCommand.getResult());
               return Collections.singletonList(new PhotoGalleryModel(pair.first, pair.second));
            })
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(photoGalleryModels -> {
               mediaPickerEventDelegate.post(new MediaAttachment(photoGalleryModels, MediaAttachment.Source.CAMERA, requestId));
               // need to call back, because this event comes from camera and picker
               // done method isn't called and picker won't close
               if (view != null) view.back();
            });


   }

   public void attachImages(List<BasePhotoPickerModel> pickedImages, int type) {
      Observable.from(pickedImages)
            .map(element -> {
               Pair<String, Size> pair = ImageUtils.generateUri(drawableUtil, element.getAbsolutePath());
               return new PhotoGalleryModel(pair.first, pair.second);
            })
            .map(photoGalleryModel -> {
               ArrayList<PhotoGalleryModel> chosenImages = new ArrayList<>();
               chosenImages.add(photoGalleryModel);
               MediaAttachment.Source source = MediaAttachment.Source.GALLERY;
               if (type == PHOTOS_TYPE_FACEBOOK) source = MediaAttachment.Source.FACEBOOK;
               return new MediaAttachment(chosenImages, source, requestId);
            })
            .subscribeOn(Schedulers.from(Executors.newScheduledThreadPool(5, new RxThreadFactory(THREAD_NAME_PREFIX))))
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(mediaAttachment -> mediaPickerEventDelegate.post(mediaAttachment),
                  error -> Timber.e(error, ""),
                  () -> view.back());
   }

   public interface View extends Presenter.View {

      boolean back();
   }
}
