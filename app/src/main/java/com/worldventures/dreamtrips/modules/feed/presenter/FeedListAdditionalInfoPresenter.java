package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.List;

public class FeedListAdditionalInfoPresenter extends FeedItemAdditionalInfoPresenter<FeedListAdditionalInfoPresenter.View> {

    public FeedListAdditionalInfoPresenter(FeedAdditionalInfoBundle args) {
        super(args);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (view.isTabletLandscape())
            loadFriends();
    }

    private void loadFriends() {
        doRequest(new GetFriendsQuery(Circle.all(context.getString(R.string.all)), null, 0), view::setupCloseFriends);
    }

    public interface View extends FeedItemAdditionalInfoPresenter.View {
        void setupCloseFriends(List<User> friends);
    }
}
