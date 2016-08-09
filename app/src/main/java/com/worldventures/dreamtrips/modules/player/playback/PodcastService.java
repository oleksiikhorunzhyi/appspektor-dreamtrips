package com.worldventures.dreamtrips.modules.player.playback;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import rx.Observable;
import timber.log.Timber;

public class PodcastService extends Service {

    private final PodcastServiceBinder binder = new PodcastServiceBinder();
    private DtPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("Podcasts -- Service -- onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public Observable<DtPlayer> constructNewPlayer(Uri uri) {
        Timber.d("Podcasts -- Service -- constructNewPlayer");
        player = new DtMediaPlayer(getApplicationContext(), uri);
        return Observable.just(player);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
        Timber.d("Podcasts -- Service -- onDestroy");
    }

    public class PodcastServiceBinder extends Binder {
        public PodcastService getPodcastService() {
            return PodcastService.this;
        }
    }
}
