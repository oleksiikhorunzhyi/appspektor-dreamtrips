package com.worldventures.dreamtrips.modules.feed.presenter;

import android.text.TextUtils;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.profile.api.GetPublicProfileQuery;

public class FeedItemAdditionalInfoPresenter<V extends FeedItemAdditionalInfoPresenter.View> extends Presenter<V> {

    User user;

    public FeedItemAdditionalInfoPresenter(User user) {
        this.user = user;
    }

    public void loadUser() {
        if (user == null) return;
        //
        if (!TextUtils.isEmpty(user.getBackgroundPhotoUrl())) {
            view.setupView(user);
        } else {
            doRequest(new GetPublicProfileQuery(user), view::setupView, spiceException -> view.setupView(user));
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public interface View extends Presenter.View {
        void setupView(User user);
    }
}
