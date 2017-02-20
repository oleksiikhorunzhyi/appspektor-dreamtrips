package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.activities;

/**
 * Created by andres.rubiano on 20/02/2017.
 */

public interface ReviewView {

   void onPostClick();
   void onBackClick();
   void showSnackbarMessage(String message);
   void showDialogMessage(String message);
   void enableInputs();
   void disableInputs();
   void onCompleteComment();

}
