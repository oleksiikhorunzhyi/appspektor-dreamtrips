package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.MergeBucketItemPhotosWithStorageCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketPostBody;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.common.model.MediaPickerAttachment;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.wallet.util.WalletFilesUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.techery.janet.CancelException;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BucketItemEditPresenter extends BucketDetailsBasePresenter<BucketItemEditPresenter.View, EntityStateHolder<BucketPhoto>> {

   private Date selectedDate;
   private boolean savingItem = false;
   private Set<AddBucketItemPhotoCommand> operationList = new HashSet<>();

   public BucketItemEditPresenter(BucketBundle bundle) {
      super(bundle);
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      bindObservables(view);
   }

   @Override
   public void onResume() {
      super.onResume();
      selectedDate = bucketItem.getTargetDate();
      List<CategoryItem> list = db.readList(SnappyRepository.CATEGORIES, CategoryItem.class);
      if (!list.isEmpty()) {
         view.setCategoryItems(list, bucketItem.getCategory());
      }
   }

   @Override
   protected void syncUI() {
      super.syncUI();
      view.bind(bucketInteractor.mergeBucketItemPhotosWithStorageCommandPipe()
            .createObservableResult(new MergeBucketItemPhotosWithStorageCommand(bucketItem.getUid(), bucketItem.getPhotos()))
            .map(Command::getResult)
            .observeOn(Schedulers.immediate())).subscribe(entityStateHolders -> {
         putCoverPhotoAsFirst(bucketItem.getPhotos());
         view.setImages(entityStateHolders);
      });
   }

   @Override
   public void handleError(Object action, Throwable error) {
      super.handleError(action, error);
      view.hideLoading();
   }

   public void saveItem() {
      view.showLoading();
      savingItem = true;

      view.bind(bucketInteractor.updatePipe()
            .createObservable(new UpdateBucketItemCommand(createBucketPostBody()))
            .observeOn(AndroidSchedulers.mainThread()))
            .subscribe(new ActionStateSubscriber<UpdateBucketItemCommand>().onSuccess(result -> {
               if (savingItem) {
                  savingItem = false;
                  view.done();
               }
            }).onFail((updateItemHttpAction, throwable) -> {
               view.hideLoading();
               super.handleError(updateItemHttpAction, throwable);
            }));
   }

   public Date getDate() {
      if (bucketItem.getTargetDate() != null) {
         return bucketItem.getTargetDate();
      } else {
         return Calendar.getInstance().getTime();
      }
   }

   public void deletePhotoRequest(BucketPhoto bucketPhoto) {
      if (!bucketItem.getPhotos().isEmpty() && bucketItem.getPhotos().contains(bucketPhoto)) {
         view.bind(bucketInteractor.deleteItemPhotoPipe()
               .createObservable(new DeleteItemPhotoCommand(bucketItem, bucketPhoto))
               .observeOn(AndroidSchedulers.mainThread())).subscribe(new ActionStateSubscriber<DeleteItemPhotoCommand>()
               .onSuccess(deleteItemPhotoAction -> view.deleteImage(EntityStateHolder.create(bucketPhoto, EntityStateHolder.State.DONE)))
               .onFail(this::handleError));
      }
   }

   public void onDateSet(int year, int month, int day) {
      String date = DateTimeUtils.convertDateToString(year, month, day);
      view.setTime(date);
      setDate(DateTimeUtils.dateFromString(date));
   }

   public void setDate(Date date) {
      this.selectedDate = date;
   }

   public void onDateClear() {
      view.setTime(context.getString(R.string.someday));
      setDate(null);
   }

   ////////////////////////////////////////
   /////// Photo picking
   ////////////////////////////////////////
   public void onPhotoCellClicked(EntityStateHolder<BucketPhoto> photoStateHolder) {
      EntityStateHolder.State state = photoStateHolder.state();
      switch (state) {
         case FAIL:
            view.deleteImage(photoStateHolder);
            startUpload(photoStateHolder);
            break;
         case PROGRESS:
            view.deleteImage(photoStateHolder);
            cancelUpload(photoStateHolder);
            break;
      }
   }

   private void startUpload(EntityStateHolder<BucketPhoto> photoStateHolder) {
      TrackingHelper.bucketPhotoAction(TrackingHelper.ACTION_BUCKET_PHOTO_UPLOAD_START, "", bucketItem.getType());
      TrackingHelper.actionBucketItemPhoto(TrackingHelper.ATTRIBUTE_UPLOAD_PHOTO, bucketItem.getUid());
      bucketInteractor.addBucketItemPhotoPipe().send(new AddBucketItemPhotoCommand(bucketItem, photoStateHolder.entity()
            .getImagePath()));
   }

   private void cancelUpload(EntityStateHolder<BucketPhoto> photoStateHolder) {
      AddBucketItemPhotoCommand addBucketItemPhotoCommand = findCommandByStateHolder(photoStateHolder);
      if (addBucketItemPhotoCommand != null) {
         bucketInteractor.addBucketItemPhotoPipe().cancel(addBucketItemPhotoCommand);
      }
   }

   public void imageSelected(MediaPickerAttachment mediaPickerAttachment) {
      Queryable.from(mediaPickerAttachment.getChosenImages())
            .map(pickerModel -> WalletFilesUtils.convertPickedPhotoToUri(pickerModel).toString())
            .forEachR(path -> bucketInteractor.addBucketItemPhotoPipe()
                  .send(new AddBucketItemPhotoCommand(bucketItem, path)));
   }

   private void bindObservables(View view) {
      bucketInteractor.addBucketItemPhotoPipe().observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<AddBucketItemPhotoCommand>().onStart(command -> {
               operationList.add(command);
               view.addItemInProgressState(command.photoEntityStateHolder());
            }).onSuccess(command -> {
               operationList.remove(command);
               view.changeItemState(command.photoEntityStateHolder());
            }).onFail((command, throwable) -> {
               operationList.remove(command);
               if (throwable instanceof CancelException) {
                  return;
               }
               view.changeItemState(command.photoEntityStateHolder());
            }));
   }

   //////////////////////
   // Common
   //////////////////////
   private int getCategoryId() {
      CategoryItem categoryItem = view.getSelectedItem();
      return categoryItem != null ? categoryItem.getId() : 0;
   }

   @NonNull
   private ImmutableBucketPostBody createBucketPostBody() {
      return ImmutableBucketPostBody.builder()
            .id(bucketItem.getUid())
            .name(view.getTitle())
            .description(view.getDescription())
            .status(view.getStatus() ? BucketItem.COMPLETED : BucketItem.NEW)
            .tags(ProjectTextUtils.getListFromString(view.getTags()))
            .friends(ProjectTextUtils.getListFromString(view.getPeople()))
            .categoryId(getCategoryId())
            .date(selectedDate)
            .build();
   }

   private AddBucketItemPhotoCommand findCommandByStateHolder(EntityStateHolder<BucketPhoto> photoEntityStateHolder) {
      return Queryable.from(operationList).firstOrDefault(element -> element.photoEntityStateHolder()
            .equals(photoEntityStateHolder));
   }

   public interface View extends BucketDetailsBasePresenter.View<EntityStateHolder<BucketPhoto>> {
      void showError();

      void setCategoryItems(List<CategoryItem> items, CategoryItem selectedItem);

      CategoryItem getSelectedItem();

      boolean getStatus();

      String getTags();

      String getPeople();

      String getTitle();

      String getDescription();

      void showMediaPicker();

      void showLoading();

      void hideLoading();

      void addItemInProgressState(EntityStateHolder<BucketPhoto> photoEntityStateHolder);

      void changeItemState(EntityStateHolder<BucketPhoto> photoEntityStateHolder);

      void deleteImage(EntityStateHolder<BucketPhoto> photoStateHolder);
   }
}
