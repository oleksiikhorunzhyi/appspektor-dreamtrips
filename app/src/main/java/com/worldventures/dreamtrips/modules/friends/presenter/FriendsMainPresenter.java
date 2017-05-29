package com.worldventures.dreamtrips.modules.friends.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.service.FriendsInteractor;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.common.model.User.Relationship.INCOMING_REQUEST;

public class FriendsMainPresenter extends Presenter<FriendsMainPresenter.View> {

   @Inject FriendsInteractor friendsInteractor;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      friendsInteractor.getRequestsPipe()
            .observeSuccessWithReplay()
            .map(getRequestsCommand -> Queryable.from(getRequestsCommand.getResult())
                  .filter(item -> item.getRelationship() == INCOMING_REQUEST)
                  .count())
            .compose(bindViewToMainComposer())
            .subscribe(view::setRecentItems);
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

   public interface View extends Presenter.View {
      void setRecentItems(int count);
   }

}
