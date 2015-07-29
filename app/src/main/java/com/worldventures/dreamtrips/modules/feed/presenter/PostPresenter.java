package com.worldventures.dreamtrips.modules.feed.presenter;

import android.text.TextUtils;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.NewPostCommand;

import icepick.Icicle;

public class PostPresenter extends Presenter<PostPresenter.View> {

    @Icicle
    String input;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setName(getAccount().getFullName());
        view.setAvatar(getAccount().getAvatar().getThumb());
    }

    public void cancel() {
        fragmentCompass.removePost();
    }

    public void post() {
        if (!TextUtils.isEmpty(input))
            doRequest(new NewPostCommand(input), jsonObject -> {
            });
    }

    public void postInputChanged(String input) {
        this.input = input;
        if (!TextUtils.isEmpty(input))
            view.enableButton();
        else view.disableButton();
    }

    public interface View extends Presenter.View {
        void setName(String userName);

        void setAvatar(String avatarUrl);

        void enableButton();

        void disableButton();
    }
}
