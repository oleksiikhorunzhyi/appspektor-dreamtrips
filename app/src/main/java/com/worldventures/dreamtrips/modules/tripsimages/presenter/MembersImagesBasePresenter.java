package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.feed.presenter.UploadingListenerPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.service.analytics.TripImageViewAnalyticsEvent;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.CommandWithTripImages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public abstract class MembersImagesBasePresenter<C extends CommandWithTripImages>
      extends TripImagesListPresenter<MembersImagesBasePresenter.View, C> implements UploadingListenerPresenter {

   @Inject MediaPickerEventDelegate mediaPickerEventDelegate;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;
   @Inject UploadingPresenterDelegate uploadingPresenterDelegate;

   protected List<PostCompoundOperationModel> postUploads;

   public MembersImagesBasePresenter() {
      this(TripImagesType.MEMBERS_IMAGES, 0);
   }

   public MembersImagesBasePresenter(TripImagesType type, int userId) {
      super(type, userId);
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      mediaPickerEventDelegate.getObservable()
            .compose(bindViewToMainComposer())
            .subscribe(mediaAttachment -> {
               if (view.isVisibleOnScreen()) //cause neighbour tab also catches this event
                  view.openCreatePhoto(mediaAttachment, getRoutingOrigin());
            });
      subscribeToBackgroundUploadingOperations();
   }

   private void subscribeToBackgroundUploadingOperations() {
      compoundOperationsInteractor.compoundOperationsPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CompoundOperationsCommand>()
                  .onSuccess(compoundOperationsCommand -> {
                     postUploads = Queryable.from(compoundOperationsCommand.getResult())
                           .cast(PostCompoundOperationModel.class).toList();
                     refreshImagesInView();
                  }));
   }

   @Override
   protected void refreshImagesInView() {
      view.setImages(photos, new UploadingPostsList(postUploads));
   }

   @NonNull
   private CreateEntityBundle.Origin getRoutingOrigin() {
      switch (type) {
         case ACCOUNT_IMAGES_FROM_PROFILE:
            return CreateEntityBundle.Origin.PROFILE_TRIP_IMAGES;
         case ACCOUNT_IMAGES:
            return CreateEntityBundle.Origin.MY_TRIP_IMAGES;
         default:
            return CreateEntityBundle.Origin.MEMBER_TRIP_IMAGES;
      }
   }

   @Override
   public void onItemClick(IFullScreenObject image) {
      super.onItemClick(image);
      analyticsInteractor.analyticsActionPipe().send(new TripImageViewAnalyticsEvent(image.getFSId()));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Uploading handling
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void onUploadResume(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadResume(compoundOperationModel);
   }

   @Override
   public void onUploadPaused(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadPaused(compoundOperationModel);
   }

   @Override
   public void onUploadRetry(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadRetry(compoundOperationModel);
   }

   @Override
   public void onUploadCancel(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadCancel(compoundOperationModel);
   }

   public abstract void onShowNewImagesClick();

   public interface View extends TripImagesListPresenter.View {
      void openCreatePhoto(MediaAttachment mediaAttachment, CreateEntityBundle.Origin photoOrigin);

      void setImages(List<IFullScreenObject> images, UploadingPostsList uploadingPostsList);

      void showNewImagesButton(String newImagesString);

      void hideNewImagesButton();
   }
}
