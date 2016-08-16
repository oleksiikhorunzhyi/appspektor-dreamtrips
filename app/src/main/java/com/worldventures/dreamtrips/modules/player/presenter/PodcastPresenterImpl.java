package com.worldventures.dreamtrips.modules.player.presenter;

import android.app.Activity;
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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class PodcastPresenterImpl extends DtlPresenterImpl<PodcastPlayerScreen, ViewState.EMPTY>
        implements PodcastPresenter {

    @Inject
    PodcastPlayerDelegate podcastPlayerDelegate;
    private Observable<ReadOnlyPlayer> playerObservable;
    private boolean screenIsScheduledToBeDestroyed;

    private Uri uri;

    public PodcastPresenterImpl(Context context, Injector injector, Uri uri) {
        super(context);
        injector.inject(this);
        this.uri = uri;
    }

    @Override
    public void attachView(PodcastPlayerScreen view) {
        super.attachView(view);
        playerObservable = podcastPlayerDelegate.createPlayer(uri)
                .replay(1).autoConnect();
        startPlayback();
        listenToStateUpdates();
        listenToProgress();
    }

    private void startPlayback() {
        playerObservable
                .compose(bindView())
                .take(1)
                .subscribe(player -> {
                    if (player.getState() == ReadOnlyPlayer.State.READY) {
                        podcastPlayerDelegate.start();
                    } else if (player.getState() == ReadOnlyPlayer.State.STOPPED
                            || player.getState() == ReadOnlyPlayer.State.RELEASED) {
                        ((Activity) getContext()).finish();
                    }
                }, e -> Timber.e(e, "Cannot create player"));
    }

    private void listenToStateUpdates() {
        playerObservable
                .flatMap(ReadOnlyPlayer::getStateObservable)
                .flatMap(stateObservable -> playerObservable.take(1))
                .compose(bindView())
                .subscribe(this::applyState);
    }

    private void listenToProgress() {
        Observable.interval(0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .flatMap(counter -> playerObservable)
                .compose(new NonNullFilter<>())
                .compose(bindView())
                .filter(player -> player.getState() != ReadOnlyPlayer.State.UNKNOWN
                        && player.getState() != ReadOnlyPlayer.State.PREPARING)
                .subscribe(player -> {
                    checkIfNeedToDestroyWithDelay(player.getState());
                    updatePlayerProgress(player);
                });
    }

    private void updatePlayerProgress(ReadOnlyPlayer player) {
        getView().setProgress(player.getDuration(), player.getCurrentPosition(),
                player.getBufferPercentage());
    }

    private void checkIfNeedToDestroyWithDelay(ReadOnlyPlayer.State state) {
        if (state == ReadOnlyPlayer.State.RELEASED || state == ReadOnlyPlayer.State.STOPPED) {
            if (!screenIsScheduledToBeDestroyed) {
                Observable.interval(2, TimeUnit.SECONDS,
                        AndroidSchedulers.mainThread())
                        .compose(bindView())
                        .subscribe(timer -> {
                            ((Activity) context).finish();
                        });
            }
            screenIsScheduledToBeDestroyed = true;
        }
    }

    private void applyState(ReadOnlyPlayer player) {
        Timber.d("Podcasts -- presenter -- state %s dur %d, pos %d", player.getState(), player.getDuration(), player.getCurrentPosition());
        ReadOnlyPlayer.State state = player.getState();
        switch (state) {
            case ERROR:
            case RELEASED:
                getView().setPlaybackFailed();
                getView().setPausePlay(false);
                break;
            case STOPPED:
                getView().setPausePlay(true);
                updatePlayerProgress(player);
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
    public void onBackPressed() {
        podcastPlayerDelegate.stop();
    }
}
