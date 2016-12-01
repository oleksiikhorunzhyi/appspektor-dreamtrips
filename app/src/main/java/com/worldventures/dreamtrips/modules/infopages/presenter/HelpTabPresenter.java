package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public class HelpTabPresenter extends Presenter {

   @Override
   public void takeView(View view) {
      super.takeView(view);
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
