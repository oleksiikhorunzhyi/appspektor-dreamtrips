package com.worldventures.dreamtrips.modules.player.playback;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.MediaController;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PodcastService extends Service {

    private static final int WIDGET_NOTIFICATION_ID = 144;

    private final PodcastServiceBinder binder = new PodcastServiceBinder();
    private Subscription activePlayerSubscription;
    private DtPlayer player;
    // player can be tried to initialized from different threads
    private volatile boolean isPreparingPlayer = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("Podcasts -- Service -- onCreate");
        startForeground(WIDGET_NOTIFICATION_ID, new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.dt_push_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.podcast_placeholder))
                .build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public Observable<DtPlayer> preparePlayer(Uri uri) {
        Timber.d("Podcasts -- Service -- getPlayer %s", uri);
        return Observable.just(player)
            .filter(player -> player != null && player.getSourceUri().equals(uri))
            .compose(new NonNullFilter<>())
            .switchIfEmpty(Observable.fromCallable(() -> constructVideoPlayerInternal(uri)))
            .flatMap(dtPlayer -> {
                Observable<DtPlayer> playerObservable = Observable.just(player);
                if (player.getState() == DtPlayer.State.UNKNOWN) {
                    playerObservable = playerObservable
                            .subscribeOn(Schedulers.io())
                            .doOnNext(player -> {
                                if (isPreparingPlayer) return;
                                isPreparingPlayer = true;
                                player.prepare();
                                isPreparingPlayer = false;
                            })
                            .observeOn(AndroidSchedulers.mainThread());
                }
                return playerObservable;
            });
    }

    public Observable<DtPlayer.State> getPlayerStateObservable(Uri uri) {
        return preparePlayer(uri).flatMap(dtPlayer -> player.getStateObservable());
    }

    private DtPlayer constructVideoPlayerInternal(Uri uri) {
        Timber.d("Podcasts -- Service -- create player %s", uri);
        stopPlayerIfNeeded();
        player = new DtMediaPlayer(this, uri);
        activePlayerSubscription = player.getStateObservable()
                .filter(state -> state == DtPlayer.State.STOPPED)
                .subscribe(state -> onPlayerCleanup());
        return player;
    }

    public void startPlayer() {
        if (player != null) player.start();
    }

    public void pausePlayer() {
        if (player != null) player.pause();
    }

    public void stopPlayer() {
        if (player != null) onPlayerCleanup();
    }

    public Observable<MediaController.MediaPlayerControl> getMediaPlayerControl(Uri uri) {
        return preparePlayer(uri)
                .map(DtPlayer::getMediaPlayerControl);
    }

    private void onPlayerCleanup() {
        Timber.d("Podcasts -- Service -- onPlayerStopped");
        stopPlayerIfNeeded();
        stopForeground(true);
        stopSelf();
    }

    private void stopPlayerIfNeeded() {
        if (activePlayerSubscription != null) {
            activePlayerSubscription.unsubscribe();
        }
        if (player != null && player.getState() != DtPlayer.State.STOPPED) {
            Timber.d("Podcasts -- Service -- stop current player");
            player.pause();
            player.release();
        }
        player = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayerIfNeeded();
        Timber.d("Podcasts -- Service -- onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Timber.d("Podcasts -- Service -- onTaskRemoved");
        onPlayerCleanup();
    }

    public class PodcastServiceBinder extends Binder {
        public PodcastService getPodcastService() {
            return PodcastService.this;
        }
    }
}
