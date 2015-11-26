package com.worldventures.dreamtrips.modules.feed.presenter;

import android.text.TextUtils;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.profile.api.GetPublicProfileQuery;

import icepick.State;

public class FeedItemAdditionalInfoPresenter<V extends FeedItemAdditionalInfoPresenter.View> extends Presenter<V> {

    @State
    FeedAdditionalInfoBundle args;

    public FeedItemAdditionalInfoPresenter(FeedAdditionalInfoBundle args) {
        this.args = args;
    }

    public void loadUser() {
        if (args == null || args.getUser() == null) return;
        //
        User user = args.getUser();
        if (!TextUtils.isEmpty(user.getBackgroundPhotoUrl())) {
            view.setUser(user);
        } else {
            doRequest(new GetPublicProfileQuery(user), view::setUser, spiceException -> view.setUser(user));
        }
    }

    public interface View extends Presenter.View {
        void setUser(User user);
    }
}
