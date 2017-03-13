package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

public interface DtlCommentReviewScreen extends DtlScreen {

    void onPostClick();

    void showSnackbarMessage(String message);

    void showDialogMessage(String message);

    void enableInputs();

    void disableInputs();

    void onBackClick();

    boolean isFromListReview();

    int getSizeComment();

    int getRatingBar();

    boolean isMinimumCharacterWrote();

    boolean isMaximumCharacterWrote();

    void finish();

    void onRefreshSuccess();

    void onRefreshProgress();

    void onRefreshError(String error);

    void showEmpty(boolean isShow);

    void sendPostReview();

    String getFingerprintId();
}
