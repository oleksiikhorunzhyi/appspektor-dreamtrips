package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlReviewsPresenter extends DtlPresenter<DtlReviewsScreen, ViewState.EMPTY> {

   void onBackPressed();

   void onAddClick();

   void loadFirstReviews();

   void getFirstPage();

   void addMoreReviews(int indexOf);

   void addNewComment();
}
