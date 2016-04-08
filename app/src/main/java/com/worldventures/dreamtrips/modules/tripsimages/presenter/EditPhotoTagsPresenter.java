package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.PhotoTagHolderManager;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;

import java.util.List;

import rx.functions.Action1;

public class EditPhotoTagsPresenter extends Presenter<Presenter.View> implements PhotoTagHolderManager.FriendRequestProxy {

    private static final int PAGE_SIZE = 100;

    @Override
    public void requestFriends(String query, int page, Action1<List<User>> act) {
        doRequest(new GetFriendsQuery(null, query, page, PAGE_SIZE), act::call);
    }
}
