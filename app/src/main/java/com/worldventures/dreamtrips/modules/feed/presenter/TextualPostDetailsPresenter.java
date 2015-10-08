package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;

public class TextualPostDetailsPresenter extends Presenter {

    private PostBundle args;

    public TextualPostDetailsPresenter(PostBundle args) {

        this.args = args;
    }
}
