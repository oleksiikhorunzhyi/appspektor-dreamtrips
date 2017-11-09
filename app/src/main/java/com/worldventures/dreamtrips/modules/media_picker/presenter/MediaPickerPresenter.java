package com.worldventures.dreamtrips.modules.media_picker.presenter;

import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.model.MediaPickerModel;
import com.worldventures.core.modules.picker.model.VideoPickerModel;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.modules.picker.util.CapturedRowMediaHelper;
import com.worldventures.core.ui.util.DrawableUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerImagesProcessedEventDelegate;

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

   private final int requestId;
   private final int videoLengthLimit;

   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject MediaPickerImagesProcessedEventDelegate mediaPickerImagesProcessedEventDelegate;
   @Inject MediaPickerInteractor mediaPickerInteractor;
   @Inject DrawableUtil drawableUtil;
   @Inject CapturedRowMediaHelper capturedRowMediaHelper;

   public MediaPickerPresenter(int requestId, int videoLengthLimit) {
      this.requestId = requestId;
      this.videoLengthLimit = videoLengthLimit;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      subsbribeToPhotoAttachments();
      subscribeToVideoAttachment(view);
   }

   private void subscribeToVideoAttachment(View view) {
      capturedRowMediaHelper.videoModelFromCameraObservable()
            .compose(bindViewToMainComposer())
            .subscribe(videoPickerModel -> {
               int videoDurationSec = (int) (videoPickerModel.getDuration() / 1000);
               if (videoDurationSec > videoLengthLimit) {
                  view.informUser(context.getString(R.string.picker_video_length_limit, videoLengthLimit));
               } else {
                  videoPickerModel.setSource(MediaPickerAttachment.Source.CAMERA);
                  mediaPickerEventDelegate.post(new MediaPickerAttachment(videoPickerModel, requestId));
                  closeMediaPicker();
               }
            }, throwable -> Timber.e(throwable, "Could not load video"));
   }

   private void subsbribeToPhotoAttachments() {
      capturedRowMediaHelper.photoModelFromCameraObservable()
            .compose(bindViewToMainComposer())
            .subscribe(photoGalleryModel -> {
               photoGalleryModel.setSource(MediaPickerAttachment.Source.CAMERA);
               mediaPickerEventDelegate.post(new MediaPickerAttachment(photoGalleryModel, requestId));
               closeMediaPicker();
            });
   }

   private void closeMediaPicker() {
      // need to call back, because this event comes from camera and picker
      // done method isn't called and picker won't close
      if (view != null) {
         view.back();
      }
   }

   public void attachMedia(List<MediaPickerModel> pickedImages, VideoPickerModel pickedVideo, int type) {
      MediaPickerAttachment.Source source = MediaPickerAttachment.Source.GALLERY;
      if (type == PHOTOS_TYPE_FACEBOOK) {
         source = MediaPickerAttachment.Source.FACEBOOK;
      }

      mediaPickerImagesProcessedEventDelegate.post(true);
      getPhotosAttachmentsObservable(pickedImages, source)
            .switchIfEmpty(getVideoAttachmentsObservable(pickedVideo, source))
            .subscribeOn(Schedulers.from(Executors.newScheduledThreadPool(5, new RxThreadFactory(THREAD_NAME_PREFIX))))
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(mediaAttachment -> mediaPickerEventDelegate.post(mediaAttachment),
                  error -> Timber.e(error, ""),
                  () -> {
                     mediaPickerImagesProcessedEventDelegate.post(false);
                     closeMediaPicker();
                  });
   }

   private Observable<MediaPickerAttachment> getPhotosAttachmentsObservable(List<MediaPickerModel> pickedImages, MediaPickerAttachment.Source source) {
      if (pickedImages == null || pickedImages.isEmpty()) {
         return Observable.empty();
      }
      return Observable.from(pickedImages)
            .map(element -> capturedRowMediaHelper.processPhotoModel(element.getAbsolutePath()))
            .map(photoGalleryModel -> {
               photoGalleryModel.setSource(source);
               return new MediaPickerAttachment(photoGalleryModel, requestId);
            });
   }

   private Observable<MediaPickerAttachment> getVideoAttachmentsObservable(VideoPickerModel videoPickerModel, MediaPickerAttachment.Source source) {
      return Observable.just(videoPickerModel)
            .map(model -> {
               videoPickerModel.setSource(source);
               return new MediaPickerAttachment(videoPickerModel, requestId);
            });
   }

   public interface View extends Presenter.View {

      boolean back();
   }
}
