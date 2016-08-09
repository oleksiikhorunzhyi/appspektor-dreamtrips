package com.worldventures.dreamtrips.modules.player.playback;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

public class PodcastService extends Service {

    private static final int WIDGET_NOTIFICATION_ID = 144;

    private final PodcastServiceBinder binder = new PodcastServiceBinder();
    private DtPlayer player;
    private Subscription activePlayerSubscription;

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

    public Observable<DtPlayer> getPlayer(Uri uri) {
        Timber.d("Podcasts -- Service -- getPlayer %s", uri);
        return Observable.just(player)
                .filter(player -> player != null && player.getSourceUri().equals(uri))
                .compose(new NonNullFilter<>())
                .switchIfEmpty(Observable.fromCallable(() -> constructVideoPlayerInternal(uri)));
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
        if (player != null) {
            Timber.d("Podcasts -- Service -- stop current player");
            player.pause();
            player.release();
        }
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
