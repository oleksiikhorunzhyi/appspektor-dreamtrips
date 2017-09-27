package com.worldventures.dreamtrips.social.ui.bucketlist.presenter;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketCoverBody;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class BucketFullscreenPresenter extends Presenter<BucketFullscreenPresenter.View> {
   @Inject BucketInteractor bucketInteractor;

   private BucketItem bucketItem;
   private BucketPhoto bucketPhoto;

   public BucketFullscreenPresenter(BucketPhoto bucketPhoto, BucketItem bucketItem) {
      this.bucketPhoto = bucketPhoto;
      this.bucketItem = bucketItem;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      if (appSessionHolder.get().get().getUser().equals(bucketItem.getOwner())) {
         BucketPhoto coverPhoto = bucketItem.getCoverPhoto();
         view.showCheckbox(coverPhoto != null && coverPhoto.equals(bucketPhoto));
         view.showDeleteBtn();
      } else {
         view.hideDeleteBtn();
         view.hideCheckBox();
      }
      view.setBucketPhoto(bucketPhoto);
      bucketInteractor.updatePipe()
            .observeSuccess()
            .map(UpdateBucketItemCommand::getResult)
            .compose(bindViewToMainComposer())
            .subscribe(item -> {
               bucketItem = item;
               if (item != null && item.getCoverPhoto() != null) {
                  view.showCheckbox(item.getCoverPhoto().equals(bucketPhoto));
               }
            });
   }


   public void onDeleteAction() {
      bucketInteractor.deleteItemPhotoPipe()
            .createObservable(new DeleteItemPhotoCommand(bucketItem, bucketPhoto))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<DeleteItemPhotoCommand>()
                  .onSuccess(deleteItemPhotoAction -> view.informUser(context.getString(R.string.photo_deleted)))
                  .onFail(this::handleError));
   }

   public void onCheckboxPressed(boolean setAsCover) {
      if (!setAsCover || bucketItem.getCoverPhoto().equals(bucketPhoto)) return;
      bucketInteractor.updatePipe()
            .createObservable(new UpdateBucketItemCommand(ImmutableBucketCoverBody.builder()
                  .id(bucketItem.getUid())
                  .status(bucketItem.getStatus())
                  .type(bucketItem.getType())
                  .coverId(bucketPhoto.getUid())
                  .build()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<UpdateBucketItemCommand>()
                  .onStart(itemAction -> view.showCoverProgress())
                  .onSuccess(itemAction -> view.hideCoverProgress())
                  .onFail((itemAction, throwable) -> {
                     view.hideCoverProgress();
                     handleError(itemAction, throwable);
                  }));
   }

   public interface View extends Presenter.View {
      void setBucketPhoto(BucketPhoto bucketPhoto);

      void showCheckbox(boolean currentCover);

      void showCoverProgress();

      void hideCoverProgress();

      void hideDeleteBtn();

      void showDeleteBtn();

      void hideCheckBox();
   }
}