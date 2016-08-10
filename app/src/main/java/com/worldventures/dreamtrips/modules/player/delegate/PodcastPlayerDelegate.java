package com.worldventures.dreamtrips.modules.player.delegate;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.widget.MediaController;

import com.worldventures.dreamtrips.modules.player.playback.DtPlayer;
import com.worldventures.dreamtrips.modules.player.playback.PodcastService;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

public class PodcastPlayerDelegate {

    private Context context;
    private PodcastService podcastService;

    public PodcastPlayerDelegate(Context context) {
        this.context = context;
    }

    public Observable<DtPlayer.State> getPlayerStateObservable(Uri uri) {
        return getPodcastService()
                .flatMap(podcastService -> podcastService.getPlayerStateObservable(uri));
    }

    public void preparePlayer(Uri uri) {
        getPodcastService().subscribe(service -> service.preparePlayer(uri).subscribe());
    }

    public void start() {
        getPodcastService().subscribe(PodcastService::startPlayer);
    }

    public void pause() {
        getPodcastService().subscribe(PodcastService::pausePlayer);
    }

    public void stop() {
        getPodcastService().subscribe(PodcastService::stopPlayer);
    }

    public Observable<MediaController.MediaPlayerControl> getMediaPlayerControl(Uri uri) {
        return getPodcastService().flatMap(service -> service.getMediaPlayerControl(uri));
    }

    private Observable<PodcastService> getPodcastService() {
        Timber.d("Podcasts -- delegate -- getPodcastService %s", podcastService);
        return Observable.just(podcastService)
                .filter(service -> service != null)
                .switchIfEmpty(getBindServiceObservable());
    }

    private Observable getBindServiceObservable() {
        return Observable.create(new Observable.OnSubscribe<PodcastService>() {
            @Override
            public void call(Subscriber<? super PodcastService> subscriber) {
                Timber.d("Podcasts -- delegate -- bind");
                // start service as onTaskRemoved() is not called for bound only services
                context.startService(new Intent(context, PodcastService.class));
                context.bindService(new Intent(context, PodcastService.class),
                        new Connection(subscriber),
                        Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT);
            }
        });
    }

    private class Connection implements ServiceConnection {
        private Subscriber<? super PodcastService> subscriber;

        public Connection(Subscriber<? super PodcastService> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Timber.d("Podcasts -- delegate -- connected!");
            if (!subscriber.isUnsubscribed()) {
                try {
                    podcastService = ((PodcastService.PodcastServiceBinder) iBinder).getPodcastService();
                    subscriber.onNext(podcastService);
                    subscriber.onCompleted();
                } catch (Exception ex) {
                    Timber.e(ex, "Error getting player");
                    subscriber.onError(ex);
                }
            }
            subscriber = null;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Timber.d("Podcasts -- delegate -- disconnected!");
            podcastService = null;
        }
    }
}
