package com.worldventures.dreamtrips.modules.media_picker.presenter;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerImagesProcessedEventDelegate;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.util.CapturedRowMediaHelper;

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
   private int videoLengthLimit;

   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject MediaPickerImagesProcessedEventDelegate mediaPickerImagesProcessedEventDelegate;
   @Inject MediaInteractor mediaInteractor;
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
                  mediaPickerEventDelegate.post(new MediaAttachment(videoPickerModel, MediaAttachment.Source.CAMERA, requestId));
                  closeMediaPicker();
               }
            }, throwable -> Timber.e(throwable, "Could not load video"));
   }

   private void subsbribeToPhotoAttachments() {
      capturedRowMediaHelper.photoModelFromCameraObservable()
            .compose(bindViewToMainComposer())
            .subscribe(photoGalleryModel -> {
               mediaPickerEventDelegate.post(new MediaAttachment(photoGalleryModel, MediaAttachment.Source.CAMERA, requestId));
               closeMediaPicker();
            });
   }

   private void closeMediaPicker() {
      // need to call back, because this event comes from camera and picker
      // done method isn't called and picker won't close
      if (view != null) view.back();
   }

   public void attachMedia(List<MediaPickerModel> pickedImages, VideoPickerModel pickedVideo, int type) {
      MediaAttachment.Source source = MediaAttachment.Source.GALLERY;
      if (type == PHOTOS_TYPE_FACEBOOK) source = MediaAttachment.Source.FACEBOOK;

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

   private Observable<MediaAttachment> getPhotosAttachmentsObservable(List<MediaPickerModel> pickedImages, MediaAttachment.Source source) {
      if (pickedImages == null || pickedImages.isEmpty()) return Observable.empty();
      return Observable.from(pickedImages)
            .map(element -> capturedRowMediaHelper.processPhotoModel(element.getAbsolutePath()))
            .map(photoGalleryModel -> new MediaAttachment(photoGalleryModel, source, requestId));
   }

   private Observable<MediaAttachment> getVideoAttachmentsObservable(VideoPickerModel videoPickerModel, MediaAttachment.Source source) {
      return Observable.just(videoPickerModel)
            .map(model -> new MediaAttachment(videoPickerModel, source, requestId));
   }

   public interface View extends Presenter.View {

      boolean back();
   }
}
