package com.worldventures.dreamtrips.modules.dtl_flow.parts.review;


import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlReviewPresenter extends DtlPresenter<DtlReviewScreen, ViewState.EMPTY> {

   void post();

   void onMediaAttached(MediaPickerAttachment attachment);

   int getRemainingPhotosCount();
}
