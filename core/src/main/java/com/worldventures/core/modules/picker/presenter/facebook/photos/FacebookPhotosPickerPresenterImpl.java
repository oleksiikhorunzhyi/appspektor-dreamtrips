package com.worldventures.core.modules.picker.presenter.facebook.photos;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.modules.facebook.service.FacebookInteractor;
import com.worldventures.core.modules.facebook.service.command.GetPhotosCommand;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.presenter.facebook.FacebookMediaPickerPresenterImpl;
import com.worldventures.core.modules.picker.view.facebook.photos.FacebookPhotosPickerView;
import com.worldventures.core.modules.picker.viewmodel.FacebookPhotoPickerViewModel;

import java.util.List;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class FacebookPhotosPickerPresenterImpl extends FacebookMediaPickerPresenterImpl<FacebookPhotosPickerView, FacebookPhotoPickerViewModel> implements FacebookPhotosPickerPresenter {

   public FacebookPhotosPickerPresenterImpl(FacebookInteractor facebookInteractor) {
      super(facebookInteractor);
   }

   @Override
   public void attachView(FacebookPhotosPickerView view) {
      super.attachView(view);
      loadItems();
   }

   @Override
   public void observeItemSource() {
      getFacebookInteractor()
            .photosPipe()
            .observe()
            .compose(getView().lifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationGetPhotos())
                  .onSuccess(getPhotosCommand -> {
                     List<FacebookPhotoPickerViewModel> photosList = Queryable
                           .from(getPhotosCommand.getResult())
                           .map(facebookPhoto -> new FacebookPhotoPickerViewModel(facebookPhoto.getImages(),
                                 facebookPhoto.isChecked(),
                                 facebookPhoto.getPickedTime()))
                           .toList();
                     getView().addItems(photosList);
                  })
                  .onFail((getPhotosCommand, throwable) -> Timber.e(throwable, "Cannot load photos"))
                  .create());
   }

   @Override
   public void attachImages() {
      final List<FacebookPhotoPickerViewModel> result = Queryable
            .from(getView().getChosenPhotos())
            .map(element -> {
               element.setSource(MediaPickerAttachment.Source.FACEBOOK);
               return element;
            })
            .toList();
      getResultPublishSubject().onNext(result);
   }

   @Override
   public void loadItems() {
      getFacebookInteractor().photosPipe().send(GetPhotosCommand.refresh(getView().getAlbumId()));
   }

   @Override
   public void loadMore() {
      getFacebookInteractor().photosPipe().send(GetPhotosCommand.loadMore(getView().getAlbumId()));
   }
}
