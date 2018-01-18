package com.worldventures.core.modules.picker.presenter.gallery;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.modules.picker.command.GetMediaFromGalleryCommand;
import com.worldventures.core.modules.picker.command.GetVideoDurationCommand;
import com.worldventures.core.modules.picker.command.VideoCapturedCommand;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.model.MediaPickerModel;
import com.worldventures.core.modules.picker.model.VideoPickerModel;
import com.worldventures.core.modules.picker.presenter.base.BaseMediaPickerPresenterImpl;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.modules.picker.service.PickImageDelegate;
import com.worldventures.core.modules.picker.util.strategy.PhotoPickLimitStrategy;
import com.worldventures.core.modules.picker.util.strategy.VideoPickLimitStrategy;
import com.worldventures.core.modules.picker.view.gallery.GalleryMediaPickerView;
import com.worldventures.core.modules.picker.viewmodel.GalleryMediaPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.GalleryPhotoPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.GalleryVideoPickerViewModel;
import com.worldventures.core.ui.util.permission.PermissionConstants;
import com.worldventures.core.ui.util.permission.PermissionDispatcher;
import com.worldventures.core.ui.util.permission.PermissionSubscriber;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class GalleryMediaPickerPresenterImpl extends BaseMediaPickerPresenterImpl<GalleryMediaPickerView, GalleryMediaPickerViewModel> implements GalleryMediaPickerPresenter {
   private final PickImageDelegate pickImageDelegate;
   private final MediaPickerInteractor mediaPickerInteractor;
   private final PermissionDispatcher permissionDispatcher;
   private final PublishSubject<List<GalleryMediaPickerViewModel>> capturedMediaPublishSubject = PublishSubject.create();

   private Subscription galleryMediaSubscription;

   public GalleryMediaPickerPresenterImpl(PickImageDelegate pickImageDelegate, MediaPickerInteractor mediaPickerInteractor,
         PermissionDispatcher permissionDispatcher) {
      this.pickImageDelegate = pickImageDelegate;
      this.mediaPickerInteractor = mediaPickerInteractor;
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

   @Override
   public void detachView(boolean retainInstance) {
      super.detachView(retainInstance);
      galleryMediaSubscription.unsubscribe();
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
      permissionDispatcher.requestPermission(PermissionConstants.CAMERA_PERMISSIONS)
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
      mediaPickerInteractor.imageCapturedPipe()
            .observeSuccess()
            .compose(getView().lifecycle())
            .map(imageCapturedCommand -> {
               final List<GalleryMediaPickerViewModel> capturedImageContainer = new ArrayList<>();
               final GalleryPhotoPickerViewModel model = new GalleryPhotoPickerViewModel(imageCapturedCommand.getResult());
               model.setSource(MediaPickerAttachment.Source.CAMERA);
               capturedImageContainer.add(model);
               return capturedImageContainer;
            })
            .subscribe(capturedMediaPublishSubject::onNext);
   }

   private void observeVideoCapture() {
      mediaPickerInteractor.videoCapturedPipe()
            .observeSuccess()
            .map(VideoCapturedCommand::getUri)
            .flatMap(uri -> mediaPickerInteractor.getVideoDurationPipe()
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
                  videoModel.setSource(MediaPickerAttachment.Source.CAMERA);
                  capturedVideoContainer.add(videoModel);
                  capturedMediaPublishSubject.onNext(capturedVideoContainer);
               }
            }, throwable -> Timber.e(throwable, "Could not load video"));
   }

   public void attachMedia() {
      final List<GalleryMediaPickerViewModel> result = Queryable
            .from(getView().getChosenMedia())
            .map(element -> {
               element.setSource(MediaPickerAttachment.Source.GALLERY);
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
   public void itemPicked(GalleryMediaPickerViewModel pickedItem, int position,
         VideoPickLimitStrategy videoPickLimitStrategy, PhotoPickLimitStrategy photoPickLimitStrategy) {
      if (pickedItem.isChecked()) {
         getView().updateItem(position);
         attachMedia();
      } else {
         List<GalleryMediaPickerViewModel> pickedItems = getView().getChosenMedia();
         boolean itemsTypeValid = pickedItems.size() == 0 || pickedItems.get(0).getType().equals(pickedItem.getType());
         if (!itemsTypeValid) {
            getView().showWrongType();
         } else if (pickedItem.getType() == MediaPickerModel.Type.PHOTO) {
            boolean limitReached = photoPickLimitStrategy.photoPickLimit() > 0
                  && pickedItems.size() >= photoPickLimitStrategy.photoPickLimit();

            if (limitReached) {
               if (photoPickLimitStrategy.photoPickLimit() == 1) {
                  getView().updateItemWithSwap(position);
                  attachMedia();
               } else {
                  getView().showPhotoLimitReached(photoPickLimitStrategy.photoPickLimit());
               }
            } else {
               getView().updateItem(position);
               attachMedia();
            }
         } else if (pickedItem.getType() == MediaPickerModel.Type.VIDEO) {
            boolean limitReached = videoPickLimitStrategy.videoPickLimit() > 0
                  && pickedItems.size() >= videoPickLimitStrategy.videoPickLimit();
            int videoLengthSeconds = (int) (((GalleryVideoPickerViewModel) pickedItem).getDuration() / 1000);
            boolean lengthLimitReached = videoLengthSeconds > videoPickLimitStrategy.videoDurationLimit();

            if (limitReached) {
               if (videoPickLimitStrategy.videoPickLimit() == 1) {
                  getView().updateItemWithSwap(position);
                  attachMedia();
               } else {
                  getView().showVideoLimitReached(videoPickLimitStrategy.videoPickLimit());
               }
            } else if (lengthLimitReached) {
               getView().showVideoDurationLimitReached(videoPickLimitStrategy.videoDurationLimit());
            } else {
               getView().updateItem(position);
               attachMedia();
            }
         }
      }
   }

   @Override
   public Observable<List<GalleryMediaPickerViewModel>> attachedItems() {
      return super.attachedItems().mergeWith(capturedMediaPublishSubject);
   }

   private void observeGalleryFetch() {
      galleryMediaSubscription = mediaPickerInteractor.getMediaFromGalleryPipe()
            .observe()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideGalleryOperationView())
                  .onSuccess(galleryCommand -> getView().addItems(populateItems(galleryCommand.getResult())))
                  .onFail((command, throwable) -> Timber.e(throwable, "Failed to load media from gallery"))
                  .create());
   }

   @Override
   public void loadItems() {
      mediaPickerInteractor.getMediaFromGalleryPipe().send(new GetMediaFromGalleryCommand(getView().isVideoEnabled()));
   }
}
