package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment_review;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlCommentReviewsPresenter extends DtlPresenter<DtlCommentReviewScreen, ViewState.EMPTY> {

   void onBackPressed();
   void navigateToDetail(String message);

   void validateComment();
}
