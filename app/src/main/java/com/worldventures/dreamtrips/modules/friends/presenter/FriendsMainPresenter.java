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
   }

   public interface View extends Presenter.View {
      void setRecentItems(int count);
   }

}
