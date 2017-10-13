package com.worldventures.dreamtrips.social.ui.feed.presenter.delegate;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.model.MediaPickerModel;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.modules.picker.model.VideoPickerModel;
import com.worldventures.core.modules.picker.command.GetMediaFromGalleryCommand;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.modules.picker.service.PickImageDelegate;
import com.worldventures.core.ui.util.permission.PermissionDispatcher;
import com.worldventures.core.ui.util.permission.PermissionSubscriber;
import com.worldventures.core.ui.util.permission.PermissionUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.config.service.command.ConfigurationCommand;
import com.worldventures.core.modules.picker.util.CapturedRowMediaHelper;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.PhotoStripView;

import io.techery.janet.Command;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.worldventures.core.ui.util.permission.PermissionConstants.CAMERA_PERMISSIONS;
import static com.worldventures.core.ui.util.permission.PermissionConstants.READ_STORAGE_PERMISSION;

public class PhotoStripDelegate {

   private static final int PHOTO_STRIP_COUNT_LIMIT = 20;

   private Injector injector;
   private MediaPickerInteractor mediaPickerInteractor;
   private AppConfigurationInteractor appConfigurationInteractor;
   private PickImageDelegate pickImageDelegate;
   private CapturedRowMediaHelper capturedRowMediaHelper;
   private PermissionDispatcher permissionDispatcher;
   private PermissionUtils permissionUtils;

   private int pickPhotoMaxCount;
   private int pickVideoMaxCount;

   private PhotoStripView photoStrip;
   private Observable.Transformer stopper;
   private Action1<MediaPickerModel> newMediaAction;
   private Action0 openPickerAction;
   private boolean videoEnabled;

   private Subscription cameraSubscription;
   private int videoAvailableLimit;
   private int photoAvailableLimit;
   private int videoMaxLength;

   public PhotoStripDelegate(Injector injector, MediaPickerInteractor mediaPickerInteractor, AppConfigurationInteractor appConfigurationInteractor,
         PickImageDelegate pickImageDelegate, CapturedRowMediaHelper capturedRowMediaHelper, PermissionDispatcher permissionDispatcher,
         PermissionUtils permissionUtils) {
      this.injector = injector;
      this.mediaPickerInteractor = mediaPickerInteractor;
      this.appConfigurationInteractor = appConfigurationInteractor;
      this.pickImageDelegate = pickImageDelegate;
      this.capturedRowMediaHelper = capturedRowMediaHelper;
      this.permissionDispatcher = permissionDispatcher;
      this.permissionUtils = permissionUtils;
   }

   public void setMaxPickLimits(int pickPhotoMaxCount, int pickVideoMaxCount) {
      this.pickPhotoMaxCount = pickPhotoMaxCount;
      this.pickVideoMaxCount = pickVideoMaxCount;
   }

   public void maintainPhotoStrip(PhotoStripView view, Observable.Transformer viewStopper, boolean videoEnabled) {
      if (pickPhotoMaxCount == 0 || pickVideoMaxCount == 0)
         throw new RuntimeException("Setting limits before maintaining photo strip is required");

      this.videoEnabled = videoEnabled;

      photoAvailableLimit = pickPhotoMaxCount;
      videoAvailableLimit = pickVideoMaxCount;

      photoStrip = view;
      stopper = viewStopper;

      photoStrip.setInjector(injector);
      photoStrip.setEventListener(provideEventListener());

      subscribeToCaptureCancellation();
   }

   public void startLoadMedia() {
      requestPermissions(READ_STORAGE_PERMISSION, true);
   }

   private void loadMedia() {
      mediaPickerInteractor.getMediaFromGalleryPipe()
            .createObservableResult(new GetMediaFromGalleryCommand(videoEnabled, PHOTO_STRIP_COUNT_LIMIT))
            .compose(bindIoToMain(stopper))
            .map(Command::getResult)
            .subscribe(photoStrip::showMedia, throwable -> Timber.e(throwable, "Error during retrieving media from phone storage"));
   }

   public void setActions(Action1<MediaPickerModel> newMediaAction, Action0 openPickerAction) {
      this.newMediaAction = newMediaAction;
      this.openPickerAction = openPickerAction;
   }

   public void removeItem(MediaPickerModel mediaPickerModel) {
      if (photoStrip.getVisibility() == VISIBLE) {
         mediaPickerModel.setChecked(false);
         photoStrip.updateMediaModel(mediaPickerModel);
      }
   }

   public void updateLimits(int photoAvailableLimit, int videoAvailableLimit) {
      this.photoAvailableLimit = photoAvailableLimit;
      this.videoAvailableLimit = videoAvailableLimit;
   }

   ///////////////////////////////////////////////////////////////////////////////////////
   ///////////// Encapsulated event listener methods
   ///////////////////////////////////////////////////////////////////////////////////////

   private PhotoStripView.EventListener provideEventListener() {
      return new PhotoStripView.EventListener() {
         @Override
         public void photoPickStatusChanged(PhotoPickerModel model) {
            PhotoStripDelegate.this.photoPickStatusChanged(model);
         }

         @Override
         public void videoPickStatusChanged(VideoPickerModel model) {
            PhotoStripDelegate.this.videoPickStatusChanged(model);
         }

         @Override
         public void openPhotoPickerRequired() {
            PhotoStripDelegate.this.openPhotoPickerRequired();
         }

         @Override
         public void openCameraRequired() {
            PhotoStripDelegate.this.openCameraRequired();
         }
      };
   }

   private void photoPickStatusChanged(PhotoPickerModel model) {
      if (checkLimitPhotoException(model) || checkTwoMediaTypeException(model)) {
         removeItem(model);
         return;
      }

      model.setSource(MediaPickerAttachment.Source.PHOTO_STRIP);
      photoAvailableLimit += model.isChecked() ? -1 : +1;
      photoStrip.updateMediaModel(model);
      newMediaAction.call(model.copy());
   }

   private void videoPickStatusChanged(VideoPickerModel model) {
      model.setSource(MediaPickerAttachment.Source.PHOTO_STRIP);
      if (checkLimitVideoException(model) || checkTwoMediaTypeException(model)) {
         removeItem(model);
      } else {
         doWithMaxLength(maxLength -> {
            if (checkMaxVideoLengthException(model, maxLength)) {
               removeItem(model);
            } else {
               videoAvailableLimit += model.isChecked() ? -1 : +1;
               photoStrip.updateMediaModel(model);
               newMediaAction.call(model);
            }
         });
      }
   }

   private void openPhotoPickerRequired() {
      if (!checkAddNewMediaException()) openPickerAction.call();
   }

   private void openCameraRequired() {
      if (checkAddNewMediaException()) return;

      requestPermissions(CAMERA_PERMISSIONS, false);
   }

   private void openCamera() {
      if (photoAvailableLimit < pickPhotoMaxCount || !videoEnabled) {
         pickImageDelegate.takePicture();
         subscribeToCapturedPhoto();
      } else {
         openCameraViaDialog();
      }
   }

   ///////////////////////////////////////////////////////////////////////////////////////
   ////////// Interact with camera
   //////////////////////////////////////////////////////////////////////////////////////

   private void openCameraViaDialog() {
      photoStrip.showChooseCameraTypeDialog(type -> {
         if (type == MediaPickerModel.Type.PHOTO) {
            pickImageDelegate.takePicture();
            subscribeToCapturedPhoto();
         } else {
            doWithMaxLength(maxDuration -> {
               subscribeToCapturedVideo(maxDuration);
               pickImageDelegate.recordVideo(maxDuration);
            });
         }
      });
   }

   private void subscribeToCapturedVideo(int videoLengthLimit) {
      cameraSubscription = capturedRowMediaHelper.videoModelFromCameraObservable()
            .compose(bindIoToMain(stopper))
            .subscribe(videoPickerModel -> {
               if (!checkMaxVideoLengthException(videoPickerModel, videoLengthLimit)) {
                  videoPickerModel.setChecked(true);
                  videoPickerModel.setSource(MediaPickerAttachment.Source.CAMERA);
                  newMediaAction.call(videoPickerModel);
               }
               unsubscribeCameraSubscription();
            });
   }

   private void subscribeToCapturedPhoto() {
      cameraSubscription = capturedRowMediaHelper.photoModelFromCameraObservable()
            .compose(bindIoToMain(stopper))
            .subscribe(photoPickerModel -> {
               photoPickerModel.setChecked(true);
               photoPickerModel.setSource(MediaPickerAttachment.Source.CAMERA);
               newMediaAction.call(photoPickerModel);
               unsubscribeCameraSubscription();
            });
   }

   private void subscribeToCaptureCancellation() {
      mediaPickerInteractor.mediaCaptureCanceledPipe()
            .observeSuccess()
            .compose(bindIoToMain(stopper))
            .subscribe(type -> unsubscribeCameraSubscription());
   }

   private void unsubscribeCameraSubscription() {
      if (cameraSubscription != null && !cameraSubscription.isUnsubscribed()) {
         cameraSubscription.unsubscribe();
         cameraSubscription = null;
      }
   }

   private void doWithMaxLength(Action1<Integer> action1) {
      if (videoMaxLength != 0) {
         action1.call(videoMaxLength);
      } else {
         videoMaxLengthObserver().subscribe(action1::call);
      }
   }

   private Observable<Integer> videoMaxLengthObserver() {
      return appConfigurationInteractor.configurationCommandActionPipe()
            .createObservableResult(new ConfigurationCommand())
            .compose(new IoToMainComposer<>())
            .map(configurationCommand -> configurationCommand.getResult()
                  .getVideoRequirement()
                  .getVideoMaxLength())
            .doOnNext(maxLength -> videoMaxLength = maxLength);
   }

   ///////////////////////////////////////////////////////////////////////////////////////
   ///////// Permissions
   //////////////////////////////////////////////////////////////////////////////////////

   private void requestPermissions(final String[] permissions, boolean showRational) {
      permissionDispatcher.requestPermission(permissions, showRational)
            .subscribe(new PermissionSubscriber()
                  .onPermissionDeniedAction(() -> permissionsDenied(permissions))
                  .onPermissionRationaleAction(() -> permissionRational(permissions))
                  .onPermissionGrantedAction(() -> permissionsGranted(permissions)));
   }

   private void permissionsDenied(String[] permissions) {
      if (permissionUtils.equals(permissions, READ_STORAGE_PERMISSION)) {
         photoStrip.setVisibility(GONE);
      }
   }

   private void permissionRational(String[] permissions) {
      photoStrip.askUserForPermissions(permissions, (askedPermissions, answer) -> {
         if (permissionUtils.equals(askedPermissions, READ_STORAGE_PERMISSION)) {
            if (answer) requestPermissions(READ_STORAGE_PERMISSION, false);
            else permissionsDenied(READ_STORAGE_PERMISSION);
         }
      });
   }

   private void permissionsGranted(String[] permissions) {
      if (permissionUtils.equals(permissions, READ_STORAGE_PERMISSION)) {
         loadMedia();
      } else if (permissionUtils.equals(permissions, CAMERA_PERMISSIONS)) {
         openCamera();
      }
   }

   ///////////////////////////////////////////////////////////////////////////////////////
   ///////////// Helper methods
   ///////////////////////////////////////////////////////////////////////////////////////

   private <T> Observable.Transformer<T, T> bindIoToMain(Observable.Transformer stopper) {
      return input -> input.compose(new IoToMainComposer<>()).compose(stopper);
   }

   private boolean checkLimitPhotoException(PhotoPickerModel model) {
      if (model.isChecked() && photoAvailableLimit == 0) {
         photoStrip.showError(R.string.picker_photo_limit_plural, pickPhotoMaxCount);
         return true;
      }

      return false;
   }

   private boolean checkLimitVideoException(VideoPickerModel model) {
      if (model.isChecked() && videoAvailableLimit == 0) {
         photoStrip.showError(R.string.picker_video_limit, pickVideoMaxCount);
         return true;
      }

      return false;
   }

   private boolean checkTwoMediaTypeException(MediaPickerModel model) {
      if (!model.isChecked()) return false;

      if ((model.getType() == MediaPickerModel.Type.VIDEO && photoAvailableLimit < pickPhotoMaxCount)
            || (model.getType() == MediaPickerModel.Type.PHOTO && videoAvailableLimit < pickVideoMaxCount)) {
         photoStrip.showError(R.string.picker_two_media_type_error);
         return true;
      } else {
         return false;
      }
   }

   private boolean checkAddNewMediaException() {
      if (videoAvailableLimit == 0 || photoAvailableLimit == 0) {
         photoStrip.showError(R.string.picker_two_media_type_error);
         return true;
      } else {
         return false;
      }
   }

   private boolean checkMaxVideoLengthException(VideoPickerModel model, long maxVideoLength) {
      int videoDurationSec = (int) (model.getDuration() / 1000);
      if (videoDurationSec > maxVideoLength) {
         photoStrip.showError(R.string.picker_video_length_limit, maxVideoLength);
         return true;
      } else {
         return false;
      }
   }

}
