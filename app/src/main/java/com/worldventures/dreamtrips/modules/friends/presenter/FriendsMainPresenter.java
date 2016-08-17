package com.worldventures.dreamtrips.modules.friends.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.events.RequestsLoadedEvent;

public class FriendsMainPresenter extends Presenter<FriendsMainPresenter.View> {

   public void onEvent(RequestsLoadedEvent event) {
      view.setRecentItems(event.getCount());
   }

   public interface View extends Presenter.View {
      void setRecentItems(int count);
   }

}
