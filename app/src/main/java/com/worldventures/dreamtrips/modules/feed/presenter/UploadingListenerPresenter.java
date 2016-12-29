package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;

public interface UploadingListenerPresenter {
   void onUploadResume(PostCompoundOperationModel compoundOperationModel);

   void onUploadPaused(PostCompoundOperationModel compoundOperationModel);

   void onUploadRetry(PostCompoundOperationModel compoundOperationModel);

   void onUploadCancel(PostCompoundOperationModel compoundOperationModel);
}
