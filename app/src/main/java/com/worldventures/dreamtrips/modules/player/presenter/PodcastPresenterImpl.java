package com.worldventures.dreamtrips.modules.player.presenter;

import android.content.Context;
import android.net.Uri;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.player.delegate.PodcastPlayerDelegate;
import com.worldventures.dreamtrips.modules.player.view.PodcastPlayerScreen;

import javax.inject.Inject;

public class PodcastPresenterImpl extends DtlPresenterImpl<PodcastPlayerScreen, ViewState.EMPTY>
        implements PodcastPresenter {

    @Inject PodcastPlayerDelegate podcastPlayerDelegate;

    private Uri uri;

    public PodcastPresenterImpl(Context context, Injector injector, Uri uri) {
        super(context);
        injector.inject(this);
        this.uri = uri;
    }

    @Override
    public void attachView(PodcastPlayerScreen view) {
        super.attachView(view);
        podcastPlayerDelegate.createPlayer(uri)
                .subscribe(player -> {
                    podcastPlayerDelegate.start();
                });
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        podcastPlayerDelegate.stop();
    }
}
