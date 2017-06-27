package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;


import android.content.Context;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.modules.common.command.GetPhotosFromGalleryCommand;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerViewModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BaseWalletPickerPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerAttachment;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class WalletGalleryPickerPresenterImpl extends BaseWalletPickerPresenterImpl<WalletGalleryPickerView> implements WalletGalleryPickerPresenter {

   private final Context context;
   private final PickImageDelegate pickImageDelegate;
   private final MediaInteractor mediaInteractor;
   private final PermissionDispatcher permissionDispatcher;

   public WalletGalleryPickerPresenterImpl(Context context, PickImageDelegate pickImageDelegate,
         MediaInteractor mediaInteractor, PermissionDispatcher permissionDispatcher) {
      this.context = context;
      this.pickImageDelegate = pickImageDelegate;
      this.mediaInteractor = mediaInteractor;
      this.permissionDispatcher = permissionDispatcher;
   }

   public Context getContext() {
      return context;
   }

   @Override
   public void attachView(WalletGalleryPickerView view) {
      super.attachView(view);
      observeImageCapture();
      observeGalleryFetch();
      loadItems();
   }

   private List<WalletGalleryPickerModel> populateItems(List<PhotoGalleryModel> commandResult) {
      final List<WalletGalleryPickerModel> appendedList = new ArrayList<>();
      appendedList.addAll(getView().provideStaticItems());
      final List<WalletGalleryPhotoModel> galleryPhotoModels = Queryable
            .from(commandResult)
            .map(photoGalleryModel ->
                  new WalletGalleryPhotoModel(photoGalleryModel.getAbsolutePath(), photoGalleryModel.getDateTaken()))
            .toList();
      appendedList.addAll(galleryPhotoModels);
      return appendedList;
   }

   void checkPermissions()  {
      permissionDispatcher.requestPermission(PermissionConstants.CAMERA_STORE_PERMISSIONS)
            .compose(getView().lifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                  new PermissionSubscriber()
                        .onPermissionRationaleAction(getView()::showRationaleForCamera)
                        .onPermissionGrantedAction(getView()::cameraPermissionGranted)
                        .onPermissionDeniedAction(getView()::showDeniedForCamera)
            );
   }

   @Override
   public void tryOpenCamera() {
      checkPermissions();
   }

   @Override
   public void openCamera() {
      pickImageDelegate.takePicture();
   }

   private void observeImageCapture() {
      mediaInteractor.imageCapturedPipe()
            .observeSuccess()
            .compose(getView().lifecycle())
            .map(imageCapturedCommand -> {
               final List<BasePickerViewModel> capturedImageContainer =  new ArrayList<>();
               capturedImageContainer.add(new WalletGalleryPhotoModel(imageCapturedCommand.getResult()));
               return new WalletPickerAttachment(WalletPickerAttachment.WalletPickerSource.CAMERA, capturedImageContainer);
            })
            .subscribe(getResultPublishSubject()::onNext);
   }

   @Override
   public void attachImages() {
      final List<BasePickerViewModel> result = Queryable
            .from(getView().getChosenPhotos())
            .map(element -> (BasePickerViewModel) element)
            .toList();
      getResultPublishSubject().onNext(new WalletPickerAttachment(WalletPickerAttachment.WalletPickerSource.GALLERY, result));
   }

   private void observeGalleryFetch() {
      mediaInteractor.getPhotosFromGalleryPipe()
            .observe()
            .compose(getView().lifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideGalleryOperationView())
                  .onSuccess(getPhotosFromGalleryCommand -> {
                     getView().addItems(populateItems(getPhotosFromGalleryCommand.getResult()));
                  })
                  .onFail((command, throwable) -> Timber.e(throwable, "Failed to load photos from gallery"))
                  .create());
   }

   @Override
   public void loadItems() {
      mediaInteractor.getPhotosFromGalleryPipe().send(new GetPhotosFromGalleryCommand(getContext()));
   }
}
