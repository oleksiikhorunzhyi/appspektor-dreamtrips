package com.worldventures.dreamtrips.social.ui.bucketlist.presenter;

import android.os.Bundle;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

import javax.inject.Inject;

public class BucketPopularTabsPresenter extends Presenter<Presenter.View> {

   public static final String EXTRA_TYPE = "EXTRA_TYPE";

   @Inject SessionActionPipeCreator actionPipeCreator;

   public Bundle getBundleForPosition(int position) {
      Bundle args = new Bundle();
      BucketItem.BucketType type = BucketItem.BucketType.values()[position];
      args.putSerializable(BucketPopularTabsPresenter.EXTRA_TYPE, type);
      return args;
   }

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