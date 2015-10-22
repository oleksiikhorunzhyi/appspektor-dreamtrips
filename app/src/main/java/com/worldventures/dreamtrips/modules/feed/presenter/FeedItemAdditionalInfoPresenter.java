package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;

public class FeedItemAdditionalInfoPresenter<V extends FeedItemAdditionalInfoPresenter.View> extends Presenter<V> {

    private FeedAdditionalInfoBundle args;

    public FeedItemAdditionalInfoPresenter(FeedAdditionalInfoBundle args) {
        this.args = args;
    }

    @Override
    public void takeView(V view) {
        super.takeView(view);
        view.setupAccount(args.getUser());

    }

    public interface View extends Presenter.View {
        void setupAccount(User user);
    }
}
