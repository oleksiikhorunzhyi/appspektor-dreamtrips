package com.worldventures.dreamtrips.modules.feed.presenter.delegate;

import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.CancelCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.PauseCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.ResumeCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.feed.presenter.UploadingListenerPresenter;

public class UploadingPresenterDelegate implements UploadingListenerPresenter {

   private BackgroundUploadingInteractor uploadingInteractor;

   public UploadingPresenterDelegate(BackgroundUploadingInteractor uploadingInteractor) {
      this.uploadingInteractor = uploadingInteractor;
   }

   @Override
   public void onUploadResume(PostCompoundOperationModel compoundOperationModel) {
      uploadingInteractor.resumeCompoundOperationPipe()
            .send(new ResumeCompoundOperationCommand(compoundOperationModel));
   }

   @Override
   public void onUploadPaused(PostCompoundOperationModel compoundOperationModel) {
      uploadingInteractor.pauseCompoundOperationPipe().send(new PauseCompoundOperationCommand(compoundOperationModel));
   }

   @Override
   public void onUploadRetry(PostCompoundOperationModel compoundOperationModel) {
      uploadingInteractor.resumeCompoundOperationPipe()
            .send(new ResumeCompoundOperationCommand(compoundOperationModel));
   }

   @Override
   public void onUploadCancel(PostCompoundOperationModel compoundOperationModel) {
      uploadingInteractor.cancelCompoundOperationPipe()
            .send(new CancelCompoundOperationCommand(compoundOperationModel));
   }
}
