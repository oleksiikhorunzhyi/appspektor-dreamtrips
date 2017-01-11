package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.BasePickerPresenter;
import com.worldventures.dreamtrips.modules.facebook.FacebookHelper;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookPhoto;
import com.worldventures.dreamtrips.modules.facebook.service.FacebookInteractor;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetPhotosCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class FacebookPhotoPresenter extends BasePickerPresenter<FacebookPhotoPresenter.View> {

   private String albumId;

   private int previousTotal;
   private boolean loading;

   @Inject FacebookHelper facebookHelper;
   @Inject FacebookInteractor facebookInteractor;

   public FacebookPhotoPresenter(String albumId) {
      super();
      this.albumId = albumId;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      facebookInteractor.photosPipe().observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetPhotosCommand>()
                  .onSuccess(getPhotosCommand -> {
                     List<FacebookPhoto> photosList = getPhotosCommand.getResult();
                     photos.addAll(photosList);
                     view.addItems(new ArrayList<>(photosList));
                  })
                  .onFail((getPhotosCommand, throwable) -> {
                     view.back();
                     handleError(getPhotosCommand, throwable);
                  }));
   }

   public void scrolled(int totalItemCount, int lastVisible) {
      if (totalItemCount > previousTotal) {
         loading = false;
         previousTotal = totalItemCount;
      }
      if (!loading && lastVisible == totalItemCount - 1) {
         requestPhotos(true);
         loading = true;
      }
   }

   public void requestPhotos(boolean fromScroll) {
      facebookInteractor.photosPipe().send(fromScroll ? GetPhotosCommand.loadMore(albumId)
            : GetPhotosCommand.refresh(albumId));
   }

   public interface View extends BasePickerPresenter.View {

      void back();
   }
}
