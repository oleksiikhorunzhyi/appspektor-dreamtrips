package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FeedListAdditionalInfoPresenter extends FeedItemAdditionalInfoPresenter<FeedListAdditionalInfoPresenter.View> {

    @Inject
    SnappyRepository db;

    public FeedListAdditionalInfoPresenter(FeedAdditionalInfoBundle args) {
        super(args);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (view.isTabletLandscape()) {
            view.setCircles(db.getCircles());
            loadFriends(1, Circle.all(context.getString(R.string.all)));
        }
    }

    public void loadFriends(int page, Circle circle) {
        doRequest(new GetFriendsQuery(circle, null, page, 20), users -> {
            if (page == 1) view.addCloseFriends(users);
            else view.addFriends(users);
        });
    }

    public void circlePicked(Circle c) {
        loadFriends(1, c);
    }


    public interface View extends FeedItemAdditionalInfoPresenter.View {
        void addCloseFriends(List<User> friends);

        void addFriends(List<User> friends);

        void setCircles(List<Circle> circles);
    }
}
