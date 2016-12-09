package com.worldventures.dreamtrips.modules.feed.presenter.delegate;

import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.feed.presenter.UploadingListenerPresenter;

import timber.log.Timber;

public class UploadingPresenterDelegate implements UploadingListenerPresenter {

   private BackgroundUploadingInteractor uploadingInteractor;

   public UploadingPresenterDelegate(BackgroundUploadingInteractor uploadingInteractor) {
      this.uploadingInteractor = uploadingInteractor;
   }

   @Override
   public void onUploadResume(PostCompoundOperationModel compoundOperationModel) {
      Timber.d("Upload -- start %s", compoundOperationModel);
   }

   @Override
   public void onUploadPaused(PostCompoundOperationModel compoundOperationModel) {
      Timber.d("Upload -- pause %s", compoundOperationModel);
   }

   @Override
   public void onUploadRetry(PostCompoundOperationModel compoundOperationModel) {
      Timber.d("Upload -- retry %s", compoundOperationModel);
   }

   @Override
   public void onUploadCancel(PostCompoundOperationModel compoundOperationModel) {
      Timber.d("Upload -- cancel %s", compoundOperationModel);
   }
}
