package com.worldventures.dreamtrips.modules.picker.presenter.gallery;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.modules.common.command.GetVideoDurationCommand;
import com.worldventures.dreamtrips.modules.common.command.VideoCapturedCommand;
import com.worldventures.dreamtrips.modules.common.delegate.PickImageDelegate;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetMediaFromGalleryCommand;
import com.worldventures.dreamtrips.modules.picker.model.GalleryMediaPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.GalleryPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.GalleryVideoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.presenter.base.BaseMediaPickerPresenterImpl;
import com.worldventures.dreamtrips.modules.picker.util.strategy.PhotoPickLimitStrategy;
import com.worldventures.dreamtrips.modules.picker.util.strategy.VideoPickLimitStrategy;
import com.worldventures.dreamtrips.modules.picker.view.gallery.GalleryMediaPickerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static rx.Observable.just;

public class GalleryMediaPickerPresenterImpl extends BaseMediaPickerPresenterImpl<GalleryMediaPickerView, GalleryMediaPickerViewModel> implements GalleryMediaPickerPresenter {
   private final PickImageDelegate pickImageDelegate;
   private final MediaInteractor mediaInteractor;
   private final PermissionDispatcher permissionDispatcher;
   private final PublishSubject<List<GalleryMediaPickerViewModel>> capturedMediaPublishSubject = PublishSubject.create();

   public GalleryMediaPickerPresenterImpl(PickImageDelegate pickImageDelegate, MediaInteractor mediaInteractor,
         PermissionDispatcher permissionDispatcher) {
      this.pickImageDelegate = pickImageDelegate;
      this.mediaInteractor = mediaInteractor;
      this.permissionDispatcher = permissionDispatcher;
   }

   @Override
   public void attachView(GalleryMediaPickerView view) {
      super.attachView(view);
      observeImageCapture();
      if (getView().isVideoEnabled()) {
         observeVideoCapture();
      }
      observeGalleryFetch();
      loadItems();
   }

   private List<GalleryMediaPickerViewModel> populateItems(List<MediaPickerModel> commandResult) {
      final List<GalleryMediaPickerViewModel> appendedList = new ArrayList<>();
      appendedList.addAll(getView().provideStaticItems());
      final List<GalleryMediaPickerViewModel> galleryPhotoModels = Queryable
            .from(commandResult)
            .map(element -> {
               if (element.getType() == MediaPickerModel.Type.PHOTO) {
                  return new GalleryPhotoPickerViewModel(element.getAbsolutePath(), element.getDateTaken());
               } else {
                  return new GalleryVideoPickerViewModel(element.getAbsolutePath(), ((VideoPickerModel) element).getDuration());
               }
            })
            .toList();
      appendedList.addAll(galleryPhotoModels);
      return appendedList;
   }

   void checkPermissions(Action0 permissionsGrantedAction) {
      permissionDispatcher.requestPermission(PermissionConstants.CAMERA_STORE_PERMISSIONS)
            .compose(getView().lifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                  new PermissionSubscriber()
                        .onPermissionRationaleAction(getView()::showRationaleForCamera)
                        .onPermissionGrantedAction(permissionsGrantedAction)
                        .onPermissionDeniedAction(getView()::showDeniedForCamera)
            );
   }

   @Override
   public void tryOpenCameraForPhoto() {
      checkPermissions(getView()::cameraPermissionGrantedPhoto);
   }

   @Override
   public void openCameraForPhoto() {
      pickImageDelegate.takePicture();
   }

   @Override
   public void tryOpenCameraForVideo() {
      checkPermissions(getView()::cameraPermissionGrantedVideo);
   }

   @Override
   public void openCameraForVideo() {
      pickImageDelegate.recordVideo(getView().getVideoDurationLimit());
   }

   private void observeImageCapture() {
      mediaInteractor.imageCapturedPipe()
            .observeSuccess()
            .compose(getView().lifecycle())
            .map(imageCapturedCommand -> {
               final List<GalleryMediaPickerViewModel> capturedImageContainer = new ArrayList<>();
               final GalleryPhotoPickerViewModel model = new GalleryPhotoPickerViewModel(imageCapturedCommand.getResult());
               model.setSource(MediaAttachment.Source.CAMERA);
               capturedImageContainer.add(model);
               return capturedImageContainer;
            })
            .subscribe(capturedMediaPublishSubject::onNext);
   }

   private void observeVideoCapture() {
      mediaInteractor.videoCapturedPipe()
            .observeSuccess()
            .map(VideoCapturedCommand::getUri)
            .flatMap(uri -> mediaInteractor.getVideoDurationPipe()
                  .createObservableResult(new GetVideoDurationCommand(uri)))
            .compose(getView().lifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .map(durationCommand -> new GalleryVideoPickerViewModel(durationCommand.getUri().getPath(),
                  durationCommand.getResult()))
            .subscribe(videoModel -> {
               int videoDurationSec = (int) (videoModel.getDuration() / 1000);
               if (videoDurationSec > getView().getVideoDurationLimit()) {
                  getView().showVideoLimitReached(getView().getVideoDurationLimit());
               } else {
                  final List<GalleryMediaPickerViewModel> capturedVideoContainer = new ArrayList<>();
                  videoModel.setSource(MediaAttachment.Source.CAMERA);
                  capturedVideoContainer.add(videoModel);
                  capturedMediaPublishSubject.onNext(capturedVideoContainer);
               }
            }, throwable -> Timber.e(throwable, "Could not load video"));
   }

   @Override
   public void attachMedia() {
      final List<GalleryMediaPickerViewModel> result = Queryable
            .from(getView().getChosenMedia())
            .map(element -> {
               element.setSource(MediaAttachment.Source.GALLERY);
               return element;
            })
            .toList();
      getResultPublishSubject().onNext(result);
   }

   @Override
   public void handleCameraClick() {
      if (getView().isVideoEnabled()) {
         getView().showAttachmentTypeDialog();
      } else {
         tryOpenCameraForPhoto();
      }
   }

   @Override
   public boolean validateItemPick(GalleryMediaPickerViewModel pickedItem, VideoPickLimitStrategy videoPickLimitStrategy, PhotoPickLimitStrategy photoPickLimitStrategy) {
      if (pickedItem.isChecked()) return true;

      List<GalleryMediaPickerViewModel> pickedItems = getView().getChosenMedia();
      boolean itemsTypeValid = pickedItems.size() == 0 || pickedItems.get(0).getType().equals(pickedItem.getType());
      if (!itemsTypeValid) {
         getView().showWrongType();
         return false;
      }

      if (pickedItem.getType() == MediaPickerModel.Type.PHOTO) {
         boolean limitReached = photoPickLimitStrategy.photoPickLimit() > 0
               && pickedItems.size() >= photoPickLimitStrategy.photoPickLimit();

         if (limitReached) {
            getView().showPhotoLimitReached(photoPickLimitStrategy.photoPickLimit());
            return false;
         }
      } else if (pickedItem.getType() == MediaPickerModel.Type.VIDEO) {
         boolean limitReached = videoPickLimitStrategy.videoPickLimit() > 0
               && pickedItems.size() >= videoPickLimitStrategy.videoPickLimit();
         int videoLengthSeconds = (int) (((GalleryVideoPickerViewModel) pickedItem).getDuration() / 1000);
         boolean lengthLimitReached = videoLengthSeconds > videoPickLimitStrategy.videoDurationLimit();

         if (limitReached) {
            getView().showVideoLimitReached(videoPickLimitStrategy.videoPickLimit());
            return false;
         }
         if (lengthLimitReached) {
            getView().showVideoDurationLimitReached(videoPickLimitStrategy.videoDurationLimit());
            return false;
         }
      }

      return true;
   }

   @Override
   public Observable<List<GalleryMediaPickerViewModel>> capturedMedia() {
      return capturedMediaPublishSubject.asObservable();
   }

   @Override
   public Observable<List<GalleryMediaPickerViewModel>> attachedItems() {
      return super.attachedItems().mergeWith(capturedMediaPublishSubject);
   }

   private void observeGalleryFetch() {
      mediaInteractor.getMediaFromGalleryPipe()
            .observe()
            .compose(getView().lifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideGalleryOperationView())
                  .onSuccess(galleryCommand -> getView().addItems(populateItems(galleryCommand.getResult())))
                  .onFail((command, throwable) -> Timber.e(throwable, "Failed to load media from gallery"))
                  .create());
   }

   @Override
   public void loadItems() {
      mediaInteractor.getMediaFromGalleryPipe().send(new GetMediaFromGalleryCommand(getView().isVideoEnabled()));
   }
}
