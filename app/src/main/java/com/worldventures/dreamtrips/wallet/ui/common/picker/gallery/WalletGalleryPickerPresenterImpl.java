package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.modules.common.command.GetVideoDurationCommand;
import com.worldventures.dreamtrips.modules.common.command.VideoCapturedCommand;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetMediaFromGalleryCommand;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerViewModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BaseWalletPickerPresenterImpl;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import timber.log.Timber;

public class WalletGalleryPickerPresenterImpl extends BaseWalletPickerPresenterImpl<WalletGalleryPickerView> implements WalletGalleryPickerPresenter {
   private final PickImageDelegate pickImageDelegate;
   private final MediaInteractor mediaInteractor;
   private final PermissionDispatcher permissionDispatcher;

   public WalletGalleryPickerPresenterImpl(PickImageDelegate pickImageDelegate, MediaInteractor mediaInteractor,
         PermissionDispatcher permissionDispatcher) {
      this.pickImageDelegate = pickImageDelegate;
      this.mediaInteractor = mediaInteractor;
      this.permissionDispatcher = permissionDispatcher;
   }

   @Override
   public void attachView(WalletGalleryPickerView view) {
      super.attachView(view);
      observeImageCapture();
      if (getView().isVideoEnabled()) {
         observeVideoCapture();
      }
      observeGalleryFetch();
      loadItems();
   }

   private List<WalletGalleryPickerModel> populateItems(List<MediaPickerModel> commandResult) {
      final List<WalletGalleryPickerModel> appendedList = new ArrayList<>();
      appendedList.addAll(getView().provideStaticItems());
      final List<WalletGalleryPickerModel> galleryPhotoModels = Queryable
            .from(commandResult)
            .map(element -> {
               if(element.getType() == MediaPickerModel.Type.PHOTO) {
                  return new WalletGalleryPhotoModel(element.getAbsolutePath(), element.getDateTaken());
               } else {
                  return new WalletGalleryVideoModel(element.getAbsolutePath(), ((VideoPickerModel) element).getDuration());
               }
            })
            .toList();
      appendedList.addAll(galleryPhotoModels);
      return appendedList;
   }

   void checkPermissions(Action0 permissionsGrantedAction)  {
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
      pickImageDelegate.recordVideo(getView().getVideoLimit());
   }

   private void observeImageCapture() {
      mediaInteractor.imageCapturedPipe()
            .observeSuccess()
            .compose(getView().lifecycle())
            .map(imageCapturedCommand -> {
               final List<BasePickerViewModel> capturedImageContainer =  new ArrayList<>();
               final WalletGalleryPhotoModel model = new WalletGalleryPhotoModel(imageCapturedCommand.getResult());
               model.setSource(MediaAttachment.Source.CAMERA);
               capturedImageContainer.add(model);
               return capturedImageContainer;
            })
            .subscribe(getResultPublishSubject()::onNext);
   }

   private void observeVideoCapture() {
      mediaInteractor.videoCapturedPipe()
            .observeSuccess()
            .map(VideoCapturedCommand::getUri)
            .flatMap(uri -> mediaInteractor.getVideoDurationPipe()
                  .createObservableResult(new GetVideoDurationCommand(uri)))
            .compose(getView().lifecycle())
            .map(durationCommand -> new WalletGalleryVideoModel(durationCommand.getUri().getPath(),
                  durationCommand.getResult()))
            .subscribe(videoModel -> {
               int videoDurationSec = (int) (videoModel.getDuration() / 1000);
               if (videoDurationSec > getView().getVideoLimit()) {
                  getView().showVideoLimitReached(getView().getVideoLimit());
               } else {
                  final List<BasePickerViewModel> capturedVideoContainer = new ArrayList<>();
                  videoModel.setSource(MediaAttachment.Source.CAMERA);
                  capturedVideoContainer.add(videoModel);
                  getResultPublishSubject().onNext(capturedVideoContainer);
               }
            }, throwable -> Timber.e(throwable, "Could not load video"));
   }

   @Override
   public void attachMedia() {
      final List<BasePickerViewModel> result = Queryable
            .from(getView().getChosenMedia())
            .map(element -> {
               element.setSource(MediaAttachment.Source.GALLERY);
               return (BasePickerViewModel) element;
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
