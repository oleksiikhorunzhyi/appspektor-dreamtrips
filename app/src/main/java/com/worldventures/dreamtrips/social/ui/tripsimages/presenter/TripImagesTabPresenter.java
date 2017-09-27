package com.worldventures.dreamtrips.social.ui.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class TripImagesTabPresenter extends Presenter {

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      subscribeToErrorUpdates();
   }

   /**
    * We show single common connection overlay over the tabs content.
    * Subscribe to offline errors to be able to handle those happened in tabs and show it.
    */
   private void subscribeToErrorUpdates() {
      offlineErrorInteractor.offlineErrorCommandPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> reportNoConnection());
   }

}
