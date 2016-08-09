package com.worldventures.dreamtrips.modules.player.presenter;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.player.delegate.PodcastPlayer;
import com.worldventures.dreamtrips.modules.player.view.PodcastPlayerScreen;

import javax.inject.Inject;

public class PodcastPresenterImpl extends DtlPresenterImpl<PodcastPlayerScreen, ViewState.EMPTY>
        implements PodcastPresenter {

    @Inject PodcastPlayer podcastPlayer;

    public PodcastPresenterImpl(Context context, Injector injector) {
        super(context);
        injector.inject(this);
    }

    @Override
    public void attachView(PodcastPlayerScreen view) {
        super.attachView(view);
        getView().attachMediaPlayerControl(podcastPlayer);
    }
}
