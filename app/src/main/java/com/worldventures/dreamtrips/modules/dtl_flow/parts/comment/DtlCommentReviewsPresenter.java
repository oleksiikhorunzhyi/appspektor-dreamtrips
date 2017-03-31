package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public interface DtlCommentReviewsPresenter extends DtlPresenter<DtlCommentReviewScreen, ViewState.EMPTY> {

    void onBackPressed();

    void navigateToDetail(String message);

    boolean validateComment();

    void sendAddReview(String description, Integer rating, boolean verified);

    int maximumCharactersAllowed();

    int minimumCharactersAllowed();

    void handleStringReview(String stringReview);
}
