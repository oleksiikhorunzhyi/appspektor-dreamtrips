package com.worldventures.dreamtrips.social.ui.feed.presenter;

import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;

public interface UploadingListenerPresenter {
   void onUploadResume(PostCompoundOperationModel compoundOperationModel);

   void onUploadPaused(PostCompoundOperationModel compoundOperationModel);

   void onUploadRetry(PostCompoundOperationModel compoundOperationModel);

   void onUploadCancel(PostCompoundOperationModel compoundOperationModel);
}
