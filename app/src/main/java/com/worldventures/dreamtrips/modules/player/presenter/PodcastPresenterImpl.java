package com.worldventures.dreamtrips.modules.player.presenter;

import android.content.Context;
import android.net.Uri;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.player.delegate.PodcastPlayerDelegate;
import com.worldventures.dreamtrips.modules.player.playback.ReadOnlyPlayer;
import com.worldventures.dreamtrips.modules.player.view.PodcastPlayerScreen;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class PodcastPresenterImpl extends DtlPresenterImpl<PodcastPlayerScreen, ViewState.EMPTY>
        implements PodcastPresenter {

    @Inject
    PodcastPlayerDelegate podcastPlayerDelegate;

    private Uri uri;

    public PodcastPresenterImpl(Context context, Injector injector, Uri uri) {
        super(context);
        injector.inject(this);
        this.uri = uri;
    }

    @Override
    public void attachView(PodcastPlayerScreen view) {
        super.attachView(view);
        startPlayback();
        listenToStateUpdates();
        listenToProgress();
    }

    private void startPlayback() {
        podcastPlayerDelegate.createPlayer(uri)
                .compose(bindView())
                .take(1)
                .subscribe(player -> {
                    if (player.getState() != ReadOnlyPlayer.State.ERROR) {
                        podcastPlayerDelegate.start();
                    }
                }, e -> Timber.e(e, "Cannot create player"));
    }

    private void listenToStateUpdates() {
        podcastPlayerDelegate.createPlayer(uri)
                .compose(new NonNullFilter<>())
                .flatMap(ReadOnlyPlayer::getStateObservable)
                .compose(bindView())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::applyState);
    }

    private void listenToProgress() {
        Observable.interval(0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .flatMap(counter -> podcastPlayerDelegate.createPlayer(uri))
                .compose(new NonNullFilter<>())
                .compose(bindView())
                .filter(player -> player.getState() != ReadOnlyPlayer.State.UNKNOWN
                        && player.getState() != ReadOnlyPlayer.State.PREPARING)
                .subscribe(player -> {
                    getView().setProgress(player.getDuration(), player.getCurrentPosition(),
                            player.getBufferPercentage());
                });
    }

    private void applyState(ReadOnlyPlayer.State state) {
        switch (state) {
            case ERROR:
            case STOPPED:
                getView().setPlaybackFailed();
                getView().setPausePlay(false);
                break;
            case PLAYING:
                getView().setPausePlay(true);
                break;
            case READY:
            case PAUSED:
                getView().setPausePlay(false);
                break;
            case UNKNOWN:
            case PREPARING:
                getView().setPreparing();
                break;
        }
    }

    @Override
    public void seekTo(int position) {
        podcastPlayerDelegate.seekTo(position);
    }

    @Override
    public void playPause() {
        podcastPlayerDelegate.createPlayer(uri)
                .subscribe(player -> {
                    if (player.getState() == ReadOnlyPlayer.State.ERROR) {
                        podcastPlayerDelegate.stop();
                        startPlayback();
                    } else if (player.isPlaying()) podcastPlayerDelegate.pause();
                    else podcastPlayerDelegate.start();
                });
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        podcastPlayerDelegate.stop();
    }
}
