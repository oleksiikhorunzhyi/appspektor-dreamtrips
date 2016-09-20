package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.FindBucketItemByPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketCoverBody;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/* TODO: send bucket item instead of bucket photo as an argument
 */
public class BucketFullscreenPresenter extends SocialFullScreenPresenter<BucketPhoto, BucketFullscreenPresenter.View> {
   @Inject BucketInteractor bucketInteractor;

   private BucketItem bucketItem;

   private boolean foreign;

   public BucketFullscreenPresenter(BucketPhoto photo, TripImagesType type, boolean foreign) {
      super(photo, type);
      this.foreign = foreign;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      view.bind(bucketInteractor.findBucketItemByPhotoActionPipe()
            .createObservable(new FindBucketItemByPhotoCommand(photo))
            .observeOn(Schedulers.immediate()))
            .subscribe(new ActionStateSubscriber<FindBucketItemByPhotoCommand>()
                  .onSuccess(findBucketItemByPhotoCommand -> bucketItem = findBucketItemByPhotoCommand.getResult()));

      bindChanges(view);
   }

   @Override
   public void onResume() {
      super.onResume();

      if (bucketItem != null && bucketItem.getCoverPhoto() != null && !foreign) {
         view.showCheckbox(bucketItem.getCoverPhoto().equals(photo));
      } else {
         view.hideCheckBox();
      }
      if (bucketItem != null) view.showDeleteBtn();
      else view.hideDeleteBtn();
   }

   @Override
   public void onDeleteAction() {
      if (bucketItem != null) {
         view.bind(bucketInteractor.deleteItemPhotoPipe()
               .createObservable(new DeleteItemPhotoCommand(bucketItem, photo))
               .observeOn(AndroidSchedulers.mainThread())).subscribe(new ActionStateSubscriber<DeleteItemPhotoCommand>()
               .onSuccess(deleteItemPhotoAction -> {
                  view.informUser(context.getString(R.string.photo_deleted));
                  eventBus.postSticky(new PhotoDeletedEvent(photo.getFSId()));
               })
               .onFail(this::handleError));
      }
   }


   public void onCheckboxPressed(boolean status) {
      if (bucketItem != null) {
         if (status && !bucketItem.getCoverPhoto().equals(photo)) {
            view.showCoverProgress();

            view.bind(bucketInteractor.updatePipe()
                  .createObservable(new UpdateItemHttpAction(ImmutableBucketCoverBody.builder()
                        .id(bucketItem.getUid())
                        .status(bucketItem.getStatus())
                        .type(bucketItem.getType())
                        .coverId(photo.getFSId())
                        .build()))
                  .observeOn(AndroidSchedulers.mainThread()))
                  .subscribe(new ActionStateSubscriber<UpdateItemHttpAction>().onSuccess(itemAction -> view.hideCoverProgress())
                        .onFail((itemAction, throwable) -> {
                           view.hideCoverProgress();
                           handleError(itemAction, throwable);
                        }));
         }
      }
   }

   private void bindChanges(View view) {
      view.bind(bucketInteractor.updatePipe()
            .observeSuccess()
            .map(UpdateItemHttpAction::getResponse)
            .observeOn(AndroidSchedulers.mainThread())).subscribe(item -> {
         if (item != null && item.getCoverPhoto() != null) {
            view.showCheckbox(item.getCoverPhoto().equals(photo));
         }

         bucketItem = item;
      });
   }

   public interface View extends SocialFullScreenPresenter.View {
      void showCheckbox(boolean show);

      void showCoverProgress();

      void hideCoverProgress();

      void hideDeleteBtn();

      void showDeleteBtn();

      void hideCheckBox();
   }
}